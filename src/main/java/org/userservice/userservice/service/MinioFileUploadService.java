package org.userservice.userservice.service;

import io.minio.*;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.userservice.userservice.config.MinioConfig;
import org.userservice.userservice.error.exception.FileUploadException;
import org.userservice.userservice.error.exception.ImageCopyFailedException;
import org.userservice.userservice.error.exception.ImageDeletionFailedException;
import org.userservice.userservice.error.exception.ImageNotFoundException;


import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.userservice.userservice.error.ErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class MinioFileUploadService {

    private final MinioConfig minioConfig;
    private final MinioClient minioClient;

    public String saveImageFile(MultipartFile file) {

        //1. image url 을 전달받아 post 요청을 처리한다.
        //2. 이때 default 사진의 경우 그 경로를 필터링하여 s3에 저장하지 않고 db 에만 저장한다.
        ///profileImg1.png
        // /profileImg2.png
        // /profileImg3.png
        // /profileImg4.png
        // /profileImg5.png
        // /profileImg6.png
        //3. db에 저장된 url과 다른 경우 업데이트치고 같으면 그냥 유지한다.
        //같은 파일이지만 사용자가 업로드를 할경우 구분이 가능할까?
        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            String uploadFilename = UUID.randomUUID() + "." + fileExtension;

            log.info("File upload started: " + uploadFilename);

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName()) // 버킷 이름 지정 (환경설정에서 가져올 수 있음)
                    .object(uploadFilename) // 업로드할 파일 이름
                    .stream(file.getInputStream(), file.getSize(), -1) // 파일 스트림
                    .contentType(file.getContentType()) // 파일 콘텐츠 타입
                    .build()
            );

            log.info("File uploaded successfully: " + uploadFilename);

            return String.format("%s/%s/%s",
                    minioConfig.getMinioUrl(),
                    minioConfig.getBucketName(),
                    uploadFilename);

        } catch (Exception e) {
            throw new FileUploadException("파일 업로드에 실패했습니다.");
        }
    }

    public String updateProfileImageByUrl(String currUrl) {
        //"http://172.16.211.113:9000/uploadimage/example.png" -> "http://172.16.211.113:9000"
        String baseUrl = currUrl.substring(0, currUrl.indexOf("/", currUrl.indexOf("://") + 3));
        //"http://172.16.211.113:9000/uploadimage/example.png" -> "example.png"
        String objectName = currUrl.substring(currUrl.lastIndexOf("/") + 1);
        log.info("baseUrl={}", baseUrl);

        // Temp bucket 에서 이미지가 있는 경우
        if (checkTempBucket(objectName)) {
            String newUrl = copyTempImageToPermanentBucket(objectName, baseUrl);
            deleteImage(minioConfig.getTempBucketName(), objectName);
            deleteImage(minioConfig.getBucketName(), objectName); //TODO: 이미지가 없는 경우 삭제 안해도됨
            return newUrl;
        } else {
            // Temp bucket 에 이미지가 없는 경우
            deleteImage(minioConfig.getBucketName(), objectName); //TODO: 이미지가 없는 경우 삭제 안해도됨
            return null;
        }
    }


    private boolean checkTempBucket(String objectName) {
        try {
            minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioConfig.getTempBucketName())
                            .object(objectName)
                            .build());
            return true; // 객체가 존재

        } catch (MinioException e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("does not exist") || errorMessage.contains("NoSuchKey")) {
                // 객체가 없는 경우
                log.warn("minio에 이미지 객체를 찾을 수 없음:  " + objectName);
                return false;
            }
            throw new ImageNotFoundException(IMAGE_NOT_FOUND, "I/O 관련 에러로 인해 이미지 찾을 수 없음");
        } catch (Exception e) {
            throw new ImageNotFoundException(IMAGE_NOT_FOUND, "기타 에러로 인해 이미지 찾을 수 없음");
        }
    }


    private String copyTempImageToPermanentBucket(String objectName, String baseUrl) {
        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .source(
                                    CopySource.builder()
                                            .bucket(minioConfig.getTempBucketName())
                                            .object(objectName)
                                            .build())
                            .build());
            // 영구 스토리지 경로 반환
            return baseUrl + "/" + minioConfig.getBucketName() + "/" + objectName;
        } catch (MinioException e) {
            throw new ImageCopyFailedException(IMAGE_COPY_FAILED, "I/O 관련 에러로 인해 이미지 복사 불가능");
        } catch (Exception e) {
            throw new ImageCopyFailedException(IMAGE_COPY_FAILED, "기타 에러로 인해 이미지 복사 불가능");
        }
    }


    private void deleteImage(String BucketName, String ObjectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(BucketName)
                            .object(ObjectName)
                            .build());
        } catch (MinioException e) {
            throw new ImageDeletionFailedException(IMAGE_DELETION_FAILED, "I/O 관련 에러로 인해 이미지 복사 불가능");
        } catch (Exception e) {
            throw new ImageDeletionFailedException(IMAGE_DELETION_FAILED, "기타 에러로 인해 이미지 복사 불가능");
        }
    }
}


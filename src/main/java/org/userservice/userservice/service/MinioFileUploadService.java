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


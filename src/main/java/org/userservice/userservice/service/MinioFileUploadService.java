package org.userservice.userservice.service;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    //1. 기본이미지를 기본이미지로 변환
    public String handleDefaultToDefault(String dbUrl, String inputUrl) {
        if (dbUrl.equals(inputUrl)) {
            log.info("기존 db url 유지");
            return dbUrl;
        } else {
            return inputUrl;
        }
    }

    //2. 기본이미지를 임시스토리지의 이미지로 변환
    public String handleDefaultToTemp(String inputUrl) {
        String baseUrl = inputUrl.substring(0, inputUrl.indexOf("/", inputUrl.indexOf("://") + 3));
        String inputObjectName = inputUrl.substring(inputUrl.lastIndexOf("/") + 1);

        String newUrl = copyTempImageToPermanentBucket(inputObjectName, baseUrl);
        deleteImage(minioConfig.getTempBucketName(), inputObjectName);
        return newUrl;
    }

    //3. 영구스토리지의 이미지를 기본이미지로 변환
    public String handlePermanentToDefault(String inputUrl, String dbUrl) {
        String dbObjectName = dbUrl.substring(dbUrl.lastIndexOf("/") + 1);
        deleteImage(minioConfig.getBucketName(), dbObjectName);
        return inputUrl;
    }

    //4. 영구스토리지의 이미지를 임시스토리지의 이미지로 변환
    public String handlePermanentToTemp(String inputUrl, String dbUrl) {
        if (inputUrl.equals(dbUrl)) {
            log.info("기존 db url 유지");
            return dbUrl;
        } else {
            String baseUrl = inputUrl.substring(0, inputUrl.indexOf("/", inputUrl.indexOf("://") + 3));   //"http://172.16.211.113:9000/uploadimage/example.png" -> "http://172.16.211.113:9000"
            String inputObjectName = inputUrl.substring(inputUrl.lastIndexOf("/") + 1);     //"http://172.16.211.113:9000/uploadimage/example.png" -> "example.png"
            String dbObjectName = dbUrl.substring(dbUrl.lastIndexOf("/") + 1);

            String newUrl = copyTempImageToPermanentBucket(inputObjectName, baseUrl);
            deleteImage(minioConfig.getTempBucketName(), inputObjectName);
            deleteImage(minioConfig.getBucketName(), dbObjectName);
            return newUrl;
        }
    }


    //TODO: 필요성 확인
    private boolean isImageInTempBucket(String objectName) {
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
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                log.warn("이미 삭제된 객체 또는 존재하지 않는 객체: {}", ObjectName);
            } else {
                throw new ImageDeletionFailedException(IMAGE_DELETION_FAILED, "MinIO 오류: " + e.getMessage());
            }
        } catch (MinioException e) {
            throw new ImageDeletionFailedException(IMAGE_DELETION_FAILED, "I/O 관련 에러로 인해 이미지 삭제 불가능");
        } catch (Exception e) {
            throw new ImageDeletionFailedException(IMAGE_DELETION_FAILED, "기타 에러로 인해 이미지 삭제 불가능");
        }
    }
}


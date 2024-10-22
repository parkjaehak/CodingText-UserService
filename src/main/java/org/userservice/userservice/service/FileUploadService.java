package org.userservice.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.userservice.userservice.config.KakaoCloudStorageConfig;
import org.userservice.userservice.error.exception.FileUploadException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileUploadService {
    private final KakaoCloudStorageConfig kakaoCloudStorageConfig;
    private final S3Client s3Client;

    public String saveImageFile(MultipartFile file) {
        String uploadedFileUrl;
        try {
            // 임시 파일 생성
            Path path = Files.createTempFile(null, null);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // 파일 이름 생성
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            String uploadFilename = UUID.randomUUID() + "." + fileExtension;

            log.info("File upload started: " + uploadFilename);

            // kakao object storage에 파일 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(kakaoCloudStorageConfig.getBucketName())
                    .key(uploadFilename)
                    .build();
            s3Client.putObject(putObjectRequest, path);

            // 업로드된 파일의 URL 생성
            uploadedFileUrl = String.format("%s/v1/%s/%s/%s",
                    kakaoCloudStorageConfig.getEndpoint(),
                    kakaoCloudStorageConfig.getProjectId(),
                    kakaoCloudStorageConfig.getBucketName(),
                    uploadFilename);

            // 임시 파일 삭제
            Files.delete(path);
        } catch (IOException e) {
            throw new FileUploadException("파일 업로드에 실패했습니다.");
        }
        return uploadedFileUrl;
    }
}



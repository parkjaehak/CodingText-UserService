package org.userservice.userservice.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.userservice.userservice.config.MinioConfig;
import org.userservice.userservice.error.exception.FileUploadException;


import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service("minio")
public class MinioFileUploadService implements FileUploadService {

    private final MinioConfig minioConfig;
    private final MinioClient minioClient;

    @Override
    public String saveImageFile(MultipartFile file) {
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
}


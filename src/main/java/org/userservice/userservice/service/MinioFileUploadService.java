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

/**
 * @삭제예정: 요구사항 수정으로 불필요한 코드
 */
@Slf4j
@RequiredArgsConstructor
@Service("minio")
public class MinioFileUploadService implements FileUploadService {

    private final MinioConfig minioConfig;
    private final MinioClient minioClient;

    @Override
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
}


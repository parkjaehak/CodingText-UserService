package org.userservice.userservice.service;

import org.springframework.web.multipart.MultipartFile;
import org.userservice.userservice.error.exception.FileUploadException;

public interface FileUploadService {
    /**
     * 업로드된 파일을 저장하고, 해당 파일의 URL을 반환합니다.
     *
     * @param file 업로드할 파일 (MultipartFile)
     * @return 저장된 파일의 URL
     * @throws FileUploadException 파일 업로드 실패 시 예외 발생
     */
    String saveImageFile(MultipartFile file);
}
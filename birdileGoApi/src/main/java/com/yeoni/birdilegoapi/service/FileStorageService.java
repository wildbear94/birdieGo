package com.yeoni.birdilegoapi.service;

import com.yeoni.birdilegoapi.exception.CustomException;
import com.yeoni.birdilegoapi.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path rootLocation;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
        } catch (Exception ex) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public String storeFile(MultipartFile file) {
        // 파일의 MIME 타입에 따라 저장 경로를 분기
        String mimeType = file.getContentType();
        Path targetDirectory;

        if (mimeType != null && mimeType.startsWith("image")) {
            targetDirectory = this.rootLocation.resolve("images");
        } else if (mimeType != null && (mimeType.equals("application/pdf") || mimeType.contains("document"))) {
            targetDirectory = this.rootLocation.resolve("documents");
        } else {
            targetDirectory = this.rootLocation.resolve("others");
        }

        // 해당 디렉토리가 없으면 생성
        try {
            Files.createDirectories(targetDirectory);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (originalFileName.contains("..")) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        String storedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
        Path targetLocation = targetDirectory.resolve(storedFileName);

        try {
            Files.copy(file.getInputStream(), targetLocation);
            // 상대 경로를 반환하여 유연성을 높임
            return targetDirectory.getFileName().toString() + "/" + storedFileName;
        } catch (IOException ex) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}

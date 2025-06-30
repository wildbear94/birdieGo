package com.yeoni.birdilegoapi.controller;

import com.yeoni.birdilegoapi.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @GetMapping("/{directory}/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String directory, @PathVariable String filename, HttpServletRequest request) {
        // 경로를 조합하여 리소스를 로드
        Resource resource = fileStorageService.loadFileAsResource(directory + "/" + filename);

        // 파일의 MIME 타입을 결정
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // contentType을 결정할 수 없는 경우 기본값 사용
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            // Content-Disposition 헤더를 'inline'으로 설정하여 브라우저에서 바로 열리도록 함
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }
}

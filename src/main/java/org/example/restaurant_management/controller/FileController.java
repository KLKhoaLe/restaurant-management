package org.example.restaurant_management.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.restaurant_management.dto.response.ApiResponse;
import org.example.restaurant_management.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class FileController {

    FileService fileService;

    @PostMapping("/media/upload")
    public ApiResponse<String> uploadMediaFile (@RequestParam("file") MultipartFile file) throws IOException {
        var status = fileService.uploadFile(file);
        return ApiResponse.<String>builder()
                .code(1000)
                .result(status)
                .build();
    }

    @GetMapping("/media/{fileName}")
    public ResponseEntity<Resource> viewImage(@PathVariable String fileName) throws IOException {
        // Lấy resource từ service
        Resource resource = fileService.getFile(fileName);

        // Lấy path thực tế để đoán content-type
        Path filePath = Paths.get("D:/upload")
                .resolve(fileName)
                .normalize()
                .toAbsolutePath();

        // Đoán MIME type: image/jpeg, image/png, ...
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            // fallback, đề phòng lỗi
            contentType = "application/octet-stream";
        }

        // Trả ảnh về (inline – trình duyệt sẽ hiển thị luôn nếu là image/*)
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

}


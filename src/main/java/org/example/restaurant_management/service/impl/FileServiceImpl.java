package org.example.restaurant_management.service.impl;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.restaurant_management.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class FileServiceImpl implements FileService {

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        Path folder = Paths.get("D:/upload");

        String fileExtension = StringUtils
                .getFilenameExtension(file.getOriginalFilename());


        String fileName = Objects.isNull(fileExtension) ?
                UUID.randomUUID().toString()
                : UUID.randomUUID() + "." + fileExtension;

        Path filePath = folder.resolve(fileName).normalize().toAbsolutePath();

        Files.copy(file.getInputStream(),filePath , StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    @Override
    public Resource getFile(String fileName) throws IOException {
        Path folder = Paths.get("D:/upload");

        Path filePath = folder.resolve(fileName)
                .normalize()
                .toAbsolutePath();

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("File not found: " + fileName);
        }

        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new IOException("Cannot read file: " + fileName);
        }

        return resource;
    }
}

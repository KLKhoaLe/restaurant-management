package org.example.restaurant_management.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    public String uploadFile (MultipartFile file) throws IOException;
    public Resource getFile (String name) throws IOException;
}

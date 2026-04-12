package com.learning.payslip.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    @Value("${payslip.storage.path}")
    private String storagePath;

    @PostConstruct
    public void init() throws IOException {
        Path path = Paths.get(storagePath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    public void saveFile(String filename, byte[] data) throws IOException {
        Path filePath = Paths.get(storagePath, filename);
        Files.write(filePath, data);
    }

    public byte[] loadFile(String filename) throws IOException {
        Path filePath = Paths.get(storagePath, filename);
        return Files.readAllBytes(filePath);
    }

    public boolean fileExists(String filename) {
        Path filePath = Paths.get(storagePath, filename);
        return Files.exists(filePath);
    }

}

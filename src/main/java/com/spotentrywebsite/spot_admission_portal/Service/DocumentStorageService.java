package com.spotentrywebsite.spot_admission_portal.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;

@Service
public class DocumentStorageService {

    // Target folder where files will be saved on your server
    private final Path rootLocation = Paths.get("uploads");

    public DocumentStorageService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize file storage directory structure!", e);
        }
    }

    public String storeFile(MultipartFile file, String candidateId, String docType) {
        if (file == null || file.isEmpty()) return null;

        try {
            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

            // Clean file naming pattern: e.g., DSE2026_0982_marksheet.pdf
            String cleanFileName = candidateId + "_" + docType + extension;
            Path destinationFile = this.rootLocation.resolve(Paths.get(cleanFileName)).toAbsolutePath();

            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
            return destinationFile.toString(); // Returns full saved path to write to DB
        } catch (IOException e) {
            throw new RuntimeException("Failed to write document file on disk storage: " + e.getMessage(), e);
        }
    }
}
package com.portfolio.porfolio.utils.Components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

/**
 * @description Reusable secure file storage component for Spring Boot applications.
 *              Protects against Path Traversal, remote code execution (RCE), and file overwrite/collision
 *              risks by enforcing strict MIME type checking, extension whitelisting, UUID renaming,
 *              and directory traversal boundary verification.
 */
@Component
public class FileStorageService {

    @Value("${file.upload-dir:upload/}")
    private String defaultUploadDir;

    /** Whitelist of permitted MIME types to prevent execution of arbitrary uploaded files */
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif",
            "image/svg+xml"
    );

    /** Whitelist of permitted image file extensions */
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".webp", ".gif", ".svg"
    );

    /**
     * @description Saves an uploaded multipart file using the default configured upload directory.
     * @param file The uploaded multipart file to be saved.
     * @return The web-accessible relative path of the saved file, or null if the file is empty.
     */
    public String saveFile(MultipartFile file) {
        return saveFile(file, this.defaultUploadDir);
    }

    /**
     * @description Saves an uploaded multipart file into a specified target directory with the following security controls:
     *              1. Rejects null or empty files.
     *              2. Validates the MIME type against the allowed whitelist.
     *              3. Validates and extracts the extension, verifying it against the allowed whitelist.
     *              4. Generates a random UUID filename, completely discarding untrusted client-supplied filenames.
     *              5. Resolves and normalizes paths, strictly verifying the target stays within the upload directory boundary.
     *              6. Safely writes the file contents to disk.
     * @param file The uploaded multipart file to be saved.
     * @param targetDirectory The directory where the file should be stored.
     * @return The web-accessible relative path of the saved file.
     * @throws IllegalArgumentException if the file is invalid or has a forbidden extension/MIME type.
     * @throws SecurityException if an unauthorized directory traversal attempt is detected.
     * @throws RuntimeException if an I/O error occurs while writing the file.
     */
    public String saveFile(MultipartFile file, String targetDirectory) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // 1. Validate MIME type against allowed whitelist
            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
                throw new IllegalArgumentException("Invalid file type. Only JPEG, PNG, WEBP, GIF, and SVG images are allowed.");
            }

            // 2. Validate and extract file extension safely
            String originalFileName = file.getOriginalFilename();
            String extension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf('.')).toLowerCase();
            }

            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                throw new IllegalArgumentException("Invalid or missing file extension: " + extension);
            }

            // 3. Resolve upload directory and create directories if they do not exist
            Path uploadPath = Paths.get(targetDirectory).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 4. Generate random UUID filename to prevent path traversal, collisions, and enumeration
            String safeFileName = UUID.randomUUID().toString() + extension;
            Path targetLocation = uploadPath.resolve(safeFileName).normalize();

            // 5. Anti-Path Traversal security check: ensure destination path stays within target directory
            if (!targetLocation.startsWith(uploadPath)) {
                throw new SecurityException("Unauthorized file path traversal attempt detected.");
            }

            // 6. Write file stream to target location
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 7. Return standard relative web path (e.g., upload/<filename>)
            String dirPrefix = targetDirectory.endsWith("/") ? targetDirectory : targetDirectory + "/";
            return dirPrefix + safeFileName;
        } catch (IOException e) {
            throw new RuntimeException("Error saving file: " + e.getMessage(), e);
        }
    }
}


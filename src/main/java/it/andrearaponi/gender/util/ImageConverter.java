package it.andrearaponi.gender.util;

import dev.langchain4j.data.image.Image;
import io.smallrye.common.constraint.NotNull;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Set;

/**
 * Utility class for image conversions and validations.
 * Provides methods to convert uploaded files to LangChain4j Image objects
 * with proper validation for AI processing.
 */
@ApplicationScoped
public class ImageConverter {

    private static final Logger LOG = Logger.getLogger(ImageConverter.class);

    private static final Set<String> SUPPORTED_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    private static final long MAX_IMAGE_SIZE_BYTES = 10 * 1024 * 1024; // 10MB

    /**
     * Converts a file upload to a LangChain4j Image object with validation.
     *
     * @param fileUpload The uploaded file
     * @return A LangChain4j Image object ready for AI processing
     * @throws IOException If there's an error reading the file
     * @throws IllegalArgumentException If the file format or size is invalid
     */
    public Image convertToLangChainImage(@NotNull FileUpload fileUpload) throws IOException {
        validateImageFile(fileUpload);

        try (InputStream is = Files.newInputStream(fileUpload.uploadedFile());
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            // Read file bytes
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            // Extract and normalize MIME type
            String mimeType = normalizeMimeType(fileUpload.contentType());

            LOG.debug(String.format("Processing image with MIME type: %s", mimeType));
            // Convert to base64 string as required by LangChain4j
            String base64Data = Base64.getEncoder().encodeToString(baos.toByteArray());

            // Build the Image object
            return Image.builder()
                    .base64Data(base64Data)
                    .mimeType(mimeType)
                    .build();
        }
    }

    /**
     * Validates that the uploaded file is a supported image type and size.
     *
     * @param fileUpload The file upload to validate
     * @throws IllegalArgumentException If validation fails
     * @throws IOException If there's an error accessing file attributes
     */
    private void validateImageFile(FileUpload fileUpload) throws IOException {
        // Check file size
        long fileSize = Files.size(fileUpload.uploadedFile());
        if (fileSize > MAX_IMAGE_SIZE_BYTES) {
            throw new IllegalArgumentException(
                    String.format("Image size exceeds maximum allowed (%d MB)", MAX_IMAGE_SIZE_BYTES / (1024 * 1024))
            );
        }

        // Check MIME type
        String mimeType = fileUpload.contentType();
        if (mimeType == null || !mimeType.startsWith("image/")) {
            throw new IllegalArgumentException("Invalid file type. Only images are supported.");
        }
    }

    /**
     * Normalizes the MIME type to one of the supported formats.
     *
     * @param mimeType The original MIME type
     * @return A normalized MIME type that is supported by OpenAI
     */
    private String normalizeMimeType(String mimeType) {
        if (SUPPORTED_MIME_TYPES.contains(mimeType)) {
            return mimeType;
        }

        // If not in supported list but is an image, default to PNG
        if (mimeType != null && mimeType.startsWith("image/")) {
            LOG.warn(String.format("Converting unsupported image format %s to image/png", mimeType));
            return "image/png";
        }

        // Fallback for safety
        return "image/png";
    }
}

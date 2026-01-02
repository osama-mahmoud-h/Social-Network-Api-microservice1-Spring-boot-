package com.app.server.utils.fileStorage.impl;

import com.app.server.exception.CustomRuntimeException;
import com.app.server.utils.fileStorage.FileProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Basic implementation of file processing service using Java ImageIO.
 *
 * NOTE: This implementation uses Java's built-in ImageIO for basic image operations.
 * For production use with advanced features, consider adding libraries like:
 * - Thumbnailator (com.github.rkalla:imgscalr-lib)
 * - ImgScalr (org.imgscalr:imgscalr-lib)
 * - TwelveMonkeys ImageIO (for better format support)
 */
@Service
@Slf4j
public class FileProcessingServiceImpl implements FileProcessingService {

    private static final List<String> SUPPORTED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/bmp", "image/webp"
    );

    private static final int MAX_WEB_IMAGE_WIDTH = 1920;
    private static final int MAX_WEB_IMAGE_HEIGHT = 1080;
    private static final float WEB_COMPRESSION_QUALITY = 0.85f;

    @Override
    public MultipartFile resizeImage(MultipartFile file, int maxWidth, int maxHeight) {
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                throw new CustomRuntimeException("Invalid image file", HttpStatus.BAD_REQUEST);
            }

            // Calculate new dimensions maintaining aspect ratio
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();

            double widthRatio = (double) maxWidth / originalWidth;
            double heightRatio = (double) maxHeight / originalHeight;
            double ratio = Math.min(widthRatio, heightRatio);

            int newWidth = (int) (originalWidth * ratio);
            int newHeight = (int) (originalHeight * ratio);

            // Don't upscale - only downscale
            if (ratio >= 1.0) {
                return file;
            }

            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resizedImage.createGraphics();

            // High quality rendering
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
            g.dispose();

            return convertBufferedImageToMultipartFile(resizedImage, file.getOriginalFilename(), "jpg");

        } catch (IOException e) {
            log.error("Error resizing image: {}", e.getMessage(), e);
            throw new CustomRuntimeException("Failed to resize image", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public MultipartFile compressImage(MultipartFile file, float quality) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new CustomRuntimeException("Invalid image file", HttpStatus.BAD_REQUEST);
            }

            // For compression with quality control, consider using Thumbnailator library
            // This is a basic implementation
            return convertBufferedImageToMultipartFile(image, file.getOriginalFilename(), "jpg");

        } catch (IOException e) {
            log.error("Error compressing image: {}", e.getMessage(), e);
            throw new CustomRuntimeException("Failed to compress image", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public MultipartFile createThumbnail(MultipartFile file, int size) {
        return resizeImage(file, size, size);
    }

    @Override
    public MultipartFile convertImageFormat(MultipartFile file, String targetFormat) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new CustomRuntimeException("Invalid image file", HttpStatus.BAD_REQUEST);
            }

            String originalFilename = file.getOriginalFilename();
            String newFilename = originalFilename != null
                ? originalFilename.replaceFirst("[.][^.]+$", "." + targetFormat)
                : "converted." + targetFormat;

            return convertBufferedImageToMultipartFile(image, newFilename, targetFormat);

        } catch (IOException e) {
            log.error("Error converting image format: {}", e.getMessage(), e);
            throw new CustomRuntimeException("Failed to convert image format", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public MultipartFile addWatermark(MultipartFile file, String watermarkText) {
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                throw new CustomRuntimeException("Invalid image file", HttpStatus.BAD_REQUEST);
            }

            Graphics2D g = originalImage.createGraphics();

            // Configure watermark appearance
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.setColor(new Color(255, 255, 255, 128)); // Semi-transparent white
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Position watermark at bottom-right
            FontMetrics fontMetrics = g.getFontMetrics();
            int textWidth = fontMetrics.stringWidth(watermarkText);
            int x = originalImage.getWidth() - textWidth - 20;
            int y = originalImage.getHeight() - 20;

            g.drawString(watermarkText, x, y);
            g.dispose();

            return convertBufferedImageToMultipartFile(originalImage, file.getOriginalFilename(), "jpg");

        } catch (IOException e) {
            log.error("Error adding watermark: {}", e.getMessage(), e);
            throw new CustomRuntimeException("Failed to add watermark", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean isValidImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        if (contentType == null || !SUPPORTED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            return false;
        }

        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            return image != null;
        } catch (IOException e) {
            log.debug("Invalid image file: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public int[] getImageDimensions(MultipartFile file) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new CustomRuntimeException("Invalid image file", HttpStatus.BAD_REQUEST);
            }

            return new int[]{image.getWidth(), image.getHeight()};

        } catch (IOException e) {
            log.error("Error reading image dimensions: {}", e.getMessage(), e);
            throw new CustomRuntimeException("Failed to read image dimensions", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public MultipartFile optimizeForWeb(MultipartFile file) {
        log.debug("Optimizing image for web: {}", file.getOriginalFilename());

        // First validate
        if (!isValidImage(file)) {
            throw new CustomRuntimeException("Invalid image file", HttpStatus.BAD_REQUEST);
        }

        // Get dimensions
        int[] dimensions = getImageDimensions(file);
        int width = dimensions[0];
        int height = dimensions[1];

        // Resize if too large
        MultipartFile processedFile = file;
        if (width > MAX_WEB_IMAGE_WIDTH || height > MAX_WEB_IMAGE_HEIGHT) {
            log.debug("Resizing image from {}x{} to max {}x{}",
                width, height, MAX_WEB_IMAGE_WIDTH, MAX_WEB_IMAGE_HEIGHT);
            processedFile = resizeImage(file, MAX_WEB_IMAGE_WIDTH, MAX_WEB_IMAGE_HEIGHT);
        }

        // Compress (basic implementation - consider Thumbnailator for better quality control)
        processedFile = compressImage(processedFile, WEB_COMPRESSION_QUALITY);

        log.info("Image optimized: {} -> {} bytes ({}% reduction)",
            file.getSize(),
            processedFile.getSize(),
            (100 - (processedFile.getSize() * 100 / file.getSize())));

        return processedFile;
    }

    /**
     * Helper method to convert BufferedImage to MultipartFile.
     */
    private MultipartFile convertBufferedImageToMultipartFile(
            BufferedImage image,
            String filename,
            String format
    ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        byte[] bytes = baos.toByteArray();

        return null;
//        return new MockMultipartFile(
//            filename,
//            filename,
//            "image/" + format,
//            new ByteArrayInputStream(bytes)
//        );
    }
}
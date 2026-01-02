package com.app.server.utils.fileStorage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * Service for processing files (resize, compress, convert, watermark, etc.)
 * Useful for image optimization, video transcoding, etc.
 */
public interface FileProcessingService {

    /**
     * Resize an image to specified dimensions while maintaining aspect ratio.
     *
     * @param file The original image file
     * @param maxWidth Maximum width in pixels
     * @param maxHeight Maximum height in pixels
     * @return Processed image as MultipartFile
     */
    MultipartFile resizeImage(MultipartFile file, int maxWidth, int maxHeight);

    /**
     * Compress an image to reduce file size.
     *
     * @param file The original image file
     * @param quality Quality factor (0.0 to 1.0, where 1.0 is highest quality)
     * @return Compressed image as MultipartFile
     */
    MultipartFile compressImage(MultipartFile file, float quality);

    /**
     * Generate a thumbnail from an image.
     *
     * @param file The original image file
     * @param size Thumbnail size in pixels (square)
     * @return Thumbnail as MultipartFile
     */
    MultipartFile createThumbnail(MultipartFile file, int size);

    /**
     * Convert image format (e.g., PNG to JPEG).
     *
     * @param file The original image file
     * @param targetFormat Target format (e.g., "jpg", "png", "webp")
     * @return Converted image as MultipartFile
     */
    MultipartFile convertImageFormat(MultipartFile file, String targetFormat);

    /**
     * Add watermark to an image.
     *
     * @param file The original image file
     * @param watermarkText Text to add as watermark
     * @return Watermarked image as MultipartFile
     */
    MultipartFile addWatermark(MultipartFile file, String watermarkText);

    /**
     * Validate if file is a valid image.
     *
     * @param file The file to validate
     * @return true if valid image
     */
    boolean isValidImage(MultipartFile file);

    /**
     * Get image dimensions without loading full image.
     *
     * @param file The image file
     * @return Array [width, height]
     */
    int[] getImageDimensions(MultipartFile file);

    /**
     * Process image for web optimization (resize + compress).
     *
     * @param file The original image file
     * @return Optimized image as MultipartFile
     */
    MultipartFile optimizeForWeb(MultipartFile file);
}
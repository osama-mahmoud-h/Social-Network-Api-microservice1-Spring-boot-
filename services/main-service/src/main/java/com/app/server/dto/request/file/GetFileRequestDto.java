package com.app.server.dto.request.file;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for file retrieval with optional inline display.
 * Uses query parameters to avoid URL encoding issues with file paths containing slashes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "File retrieval request parameters")
public class GetFileRequestDto {

    /**
     * Full file path in storage (e.g., "posts/12345-image.jpg", "profiles/user-avatar.png")
     */
    @NotBlank(message = "Filename is required")
    @Schema(
        description = "File object name in storage including folder prefix",
        example = "posts/e1980a75-1d8a-41c6-a963-12f01d9318fc.pdf",
        required = true
    )
    private String filename;

    /**
     * Whether to display the file inline (in browser) or force download.
     * Default is true (inline display).
     */
    @Schema(
        description = "Display file inline (true) or force download (false)",
        example = "true",
        defaultValue = "true"
    )
    @Builder.Default
    private Boolean inline = true;
}
package com.app.server.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaginationRequestDto {
    @NotNull(message = "Page number must not be null")
    @Min(value = 0, message = "Page number must be greater than or equal to 0")
    @Schema(
            description = "Page number (0-based)",
            example = "0",
            defaultValue = "0"
    )
    private Integer page = 0;

    @NotNull(message = "Size must not be null")
    @Min(value = 1, message = "Size must be greater than or equal to 1")
    @Schema(
            description = "Page size",
            example = "10",
            defaultValue = "10"
    )
    private Integer size = 10;
}

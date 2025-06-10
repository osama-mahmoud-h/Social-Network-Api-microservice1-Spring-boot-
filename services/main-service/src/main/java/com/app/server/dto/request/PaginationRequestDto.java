package com.app.server.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaginationRequestDto {
    @NotNull(message = "Page number must not be null")
    @Min(value = 0, message = "Page number must be greater than or equal to 0")
    private Integer page;

    @NotNull(message = "Size must not be null")
    @Min(value = 1, message = "Size must be greater than or equal to 1")
    private Integer size;
}

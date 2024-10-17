package com.example.server.dto.request;


import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;

@Data
public class PaginationRequestDto {
    @Size(min = 1, message = "Page number must be greater than 0")
    private int page;

    @Size(min = 1, message = "Size must be greater than 0")
    private int size;
}

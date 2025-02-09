package com.app.server.dto.request.profile;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileBioRequestDto {
    @NotBlank(message = "bio is required")
    @Size(min = 3, max = 255, message = "bio must be between 3 and 255 characters")
    private String bio;
}

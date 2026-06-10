package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDTO(

        @NotBlank(message = "Category name is required")
        String name
) {
}


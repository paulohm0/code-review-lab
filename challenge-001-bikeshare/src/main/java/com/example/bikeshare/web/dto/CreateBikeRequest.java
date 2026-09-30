package com.example.bikeshare.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateBikeRequest(@NotBlank String model) {
}

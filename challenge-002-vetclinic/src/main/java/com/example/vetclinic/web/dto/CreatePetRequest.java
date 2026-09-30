package com.example.vetclinic.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreatePetRequest(
        @NotNull Long tutorId,
        @NotBlank String name,
        @NotBlank String species,
        @PositiveOrZero Integer ageInYears) {
}

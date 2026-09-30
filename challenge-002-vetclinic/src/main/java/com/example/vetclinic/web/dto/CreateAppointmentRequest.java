package com.example.vetclinic.web.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

public record CreateAppointmentRequest(
        @NotNull Long petId,
        @NotNull Long veterinarianId,
        @NotNull @Future LocalDateTime scheduledAt,
        Boolean emergency) {
}

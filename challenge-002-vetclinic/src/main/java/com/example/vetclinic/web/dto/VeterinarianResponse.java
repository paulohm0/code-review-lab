package com.example.vetclinic.web.dto;

import com.example.vetclinic.domain.Veterinarian;

public record VeterinarianResponse(Long id, String name, String specialty, double consultationFee) {

    public static VeterinarianResponse from(Veterinarian veterinarian) {
        return new VeterinarianResponse(veterinarian.getId(), veterinarian.getName(), veterinarian.getSpecialty(),
                veterinarian.getConsultationFee());
    }
}

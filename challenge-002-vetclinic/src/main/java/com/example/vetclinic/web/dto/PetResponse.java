package com.example.vetclinic.web.dto;

import com.example.vetclinic.domain.Pet;

public record PetResponse(Long id, String name, String species, Integer ageInYears, Long tutorId) {

    public static PetResponse from(Pet pet) {
        return new PetResponse(pet.getId(), pet.getName(), pet.getSpecies(), pet.getAgeInYears(),
                pet.getTutor().getId());
    }
}

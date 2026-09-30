package com.example.vetclinic.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.vetclinic.domain.Pet;
import com.example.vetclinic.domain.Tutor;
import com.example.vetclinic.exception.NotFoundException;
import com.example.vetclinic.repository.PetRepository;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final TutorService tutorService;

    public PetService(PetRepository petRepository, TutorService tutorService) {
        this.petRepository = petRepository;
        this.tutorService = tutorService;
    }

    public Pet create(Long tutorId, String name, String species, Integer ageInYears) {
        Tutor tutor = tutorService.findById(tutorId);
        return petRepository.save(new Pet(name, species, ageInYears, tutor));
    }

    public Pet findById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pet não encontrado: " + id));
    }

    public List<Pet> findByTutor(Long tutorId) {
        return petRepository.findByTutorId(tutorId);
    }
}

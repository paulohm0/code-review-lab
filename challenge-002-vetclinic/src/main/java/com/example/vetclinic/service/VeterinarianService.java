package com.example.vetclinic.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.vetclinic.domain.Veterinarian;
import com.example.vetclinic.exception.NotFoundException;
import com.example.vetclinic.repository.VeterinarianRepository;

@Service
public class VeterinarianService {

    private final VeterinarianRepository veterinarianRepository;

    public VeterinarianService(VeterinarianRepository veterinarianRepository) {
        this.veterinarianRepository = veterinarianRepository;
    }

    public List<Veterinarian> findAll() {
        return veterinarianRepository.findAll();
    }

    public Veterinarian findById(Long id) {
        return veterinarianRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Veterinário não encontrado: " + id));
    }
}

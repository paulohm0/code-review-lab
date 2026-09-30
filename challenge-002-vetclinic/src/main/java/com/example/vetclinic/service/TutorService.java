package com.example.vetclinic.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.example.vetclinic.domain.Tutor;
import com.example.vetclinic.exception.ConflictException;
import com.example.vetclinic.exception.NotFoundException;
import com.example.vetclinic.repository.TutorRepository;

@Service
public class TutorService {

    private final TutorRepository tutorRepository;

    public TutorService(TutorRepository tutorRepository) {
        this.tutorRepository = tutorRepository;
    }

    public Tutor create(String name, String email, String document, String phone) {
        try {
            return tutorRepository.saveAndFlush(new Tutor(name, email, document, phone));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Já existe um tutor com este e-mail ou documento");
        }
    }

    public Tutor findById(Long id) {
        return tutorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tutor não encontrado: " + id));
    }

    public List<Tutor> findAll() {
        return tutorRepository.findAll();
    }
}

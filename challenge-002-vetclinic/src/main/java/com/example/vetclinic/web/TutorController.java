package com.example.vetclinic.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.vetclinic.domain.Tutor;
import com.example.vetclinic.service.TutorService;
import com.example.vetclinic.web.dto.CreateTutorRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tutors")
public class TutorController {

    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Tutor create(@Valid @RequestBody CreateTutorRequest request) {
        return tutorService.create(request.name(), request.email(), request.document(), request.phone());
    }

    @GetMapping
    public List<Tutor> list() {
        return tutorService.findAll();
    }

    @GetMapping("/{id}")
    public Tutor get(@PathVariable Long id) {
        return tutorService.findById(id);
    }
}

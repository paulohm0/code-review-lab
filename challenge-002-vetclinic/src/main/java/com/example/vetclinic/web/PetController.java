package com.example.vetclinic.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.vetclinic.service.PetService;
import com.example.vetclinic.web.dto.CreatePetRequest;
import com.example.vetclinic.web.dto.PetResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PetResponse create(@Valid @RequestBody CreatePetRequest request) {
        return PetResponse.from(petService.create(request.tutorId(), request.name(), request.species(),
                request.ageInYears()));
    }

    @GetMapping("/{id}")
    public PetResponse get(@PathVariable Long id) {
        return PetResponse.from(petService.findById(id));
    }

    @GetMapping
    public List<PetResponse> listByTutor(@RequestParam Long tutorId) {
        return petService.findByTutor(tutorId).stream().map(PetResponse::from).toList();
    }
}

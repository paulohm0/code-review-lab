package com.example.vetclinic.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vetclinic.service.VeterinarianService;
import com.example.vetclinic.web.dto.VeterinarianResponse;

@RestController
@RequestMapping("/veterinarians")
public class VeterinarianController {

    private final VeterinarianService veterinarianService;

    public VeterinarianController(VeterinarianService veterinarianService) {
        this.veterinarianService = veterinarianService;
    }

    @GetMapping
    public List<VeterinarianResponse> list() {
        return veterinarianService.findAll().stream().map(VeterinarianResponse::from).toList();
    }
}

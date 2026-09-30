package com.example.vetclinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.vetclinic.domain.Veterinarian;

public interface VeterinarianRepository extends JpaRepository<Veterinarian, Long> {
}

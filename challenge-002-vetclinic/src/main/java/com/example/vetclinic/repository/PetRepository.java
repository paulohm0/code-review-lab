package com.example.vetclinic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.vetclinic.domain.Pet;

public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByTutorId(Long tutorId);
}

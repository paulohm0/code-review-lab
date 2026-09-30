package com.example.vetclinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.vetclinic.domain.Tutor;

public interface TutorRepository extends JpaRepository<Tutor, Long> {
}

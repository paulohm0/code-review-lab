package com.example.vetclinic.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.vetclinic.domain.Appointment;
import com.example.vetclinic.domain.AppointmentStatus;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByVeterinarianIdAndScheduledAtAndStatusNot(Long veterinarianId, LocalDateTime scheduledAt,
            AppointmentStatus status);

    List<Appointment> findByVeterinarianIdAndScheduledAtBetweenOrderByScheduledAt(Long veterinarianId,
            LocalDateTime from, LocalDateTime to);
}

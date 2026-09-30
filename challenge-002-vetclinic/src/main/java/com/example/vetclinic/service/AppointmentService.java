package com.example.vetclinic.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.vetclinic.domain.Appointment;
import com.example.vetclinic.domain.AppointmentStatus;
import com.example.vetclinic.exception.NotFoundException;
import com.example.vetclinic.repository.AppointmentRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public Appointment findById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Consulta não encontrada: " + id));
    }

    public List<Appointment> findByVeterinarianAndDay(Long veterinarianId, LocalDate day) {
        return appointmentRepository.findByVeterinarianIdAndScheduledAtBetweenOrderByScheduledAt(
                veterinarianId, day.atStartOfDay(), day.plusDays(1).atStartOfDay());
    }

    public boolean existsAt(Long veterinarianId, LocalDateTime scheduledAt) {
        return appointmentRepository.existsByVeterinarianIdAndScheduledAtAndStatusNot(
                veterinarianId, scheduledAt, AppointmentStatus.CANCELLED);
    }

    public Appointment save(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }
}

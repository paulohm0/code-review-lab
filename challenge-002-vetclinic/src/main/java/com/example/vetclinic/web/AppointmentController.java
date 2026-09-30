package com.example.vetclinic.web;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.vetclinic.domain.Appointment;
import com.example.vetclinic.domain.AppointmentStatus;
import com.example.vetclinic.domain.Pet;
import com.example.vetclinic.domain.Veterinarian;
import com.example.vetclinic.exception.BusinessException;
import com.example.vetclinic.exception.ConflictException;
import com.example.vetclinic.service.AppointmentService;
import com.example.vetclinic.service.NotificationService;
import com.example.vetclinic.service.PetService;
import com.example.vetclinic.service.VeterinarianService;
import com.example.vetclinic.web.dto.CreateAppointmentRequest;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final PetService petService;
    private final VeterinarianService veterinarianService;
    private final NotificationService notificationService;
    private final Clock clock;

    public AppointmentController(AppointmentService appointmentService, PetService petService,
            VeterinarianService veterinarianService, NotificationService notificationService, Clock clock) {
        this.appointmentService = appointmentService;
        this.petService = petService;
        this.veterinarianService = veterinarianService;
        this.notificationService = notificationService;
        this.clock = clock;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> schedule(@RequestBody CreateAppointmentRequest request) {
        Pet pet = petService.findById(request.petId());
        Veterinarian vet = veterinarianService.findById(request.veterinarianId());
        LocalDateTime when = request.scheduledAt();
        boolean emergency = Boolean.TRUE.equals(request.emergency());

        // elective appointments only during clinic hours
        if (!emergency && (when.getHour() < 8 || when.getHour() >= 18)) {
            throw new BusinessException("Consultas eletivas só podem ser agendadas entre 08:00 e 18:00");
        }

        if (appointmentService.existsAt(vet.getId(), when)) {
            throw new ConflictException("Veterinário já possui consulta neste horário");
        }

        double fee = vet.getConsultationFee();
        if (emergency) {
            fee = fee * 1.5;
        }

        Appointment appointment = appointmentService.save(new Appointment(pet, vet, when, emergency, fee));
        notificationService.notifyScheduled(appointment);
        return ResponseEntity.status(HttpStatus.CREATED).body(toMap(appointment));
    }

    @GetMapping("/{id}")
    public Map<String, Object> get(@PathVariable Long id) {
        return toMap(appointmentService.findById(id));
    }

    @GetMapping
    public List<Map<String, Object>> listByDay(@RequestParam Long veterinarianId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return appointmentService.findByVeterinarianAndDay(veterinarianId, date).stream()
                .map(this::toMap)
                .toList();
    }

    @PostMapping("/{id}/cancel")
    public Map<String, Object> cancel(@PathVariable Long id) {
        Appointment appointment = appointmentService.findById(id);
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessException("Consulta já cancelada");
        }

        // cancelling with less than 24h notice keeps half of the fee
        long hoursUntil = Duration.between(LocalDateTime.now(clock), appointment.getScheduledAt()).toHours();
        if (hoursUntil < 24) {
            appointment.setFee(appointment.getFee() * 0.5);
        } else {
            appointment.setFee(0.0);
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);

        appointmentService.save(appointment);
        notificationService.notifyCancelled(appointment);
        return toMap(appointment);
    }

    @PostMapping("/{id}/complete")
    public Map<String, Object> complete(@PathVariable Long id, @RequestParam(required = false) String notes) {
        Appointment appointment = appointmentService.findById(id);
        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new BusinessException("Apenas consultas agendadas podem ser concluídas");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setNotes(notes);

        appointmentService.save(appointment);
        notificationService.notifyCompleted(appointment);
        return toMap(appointment);
    }

    private Map<String, Object> toMap(Appointment appointment) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id", appointment.getId());
        body.put("petName", appointment.getPet().getName());
        body.put("veterinarianName", appointment.getVeterinarian().getName());
        body.put("scheduledAt", appointment.getScheduledAt());
        body.put("status", appointment.getStatus());
        body.put("emergency", appointment.isEmergency());
        body.put("fee", appointment.getFee());
        body.put("notes", appointment.getNotes());
        return body;
    }
}

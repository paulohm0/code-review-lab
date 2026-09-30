package com.example.vetclinic;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;

import com.example.vetclinic.domain.Pet;
import com.example.vetclinic.domain.Tutor;
import com.example.vetclinic.domain.Veterinarian;
import com.example.vetclinic.repository.AppointmentRepository;
import com.example.vetclinic.repository.PetRepository;
import com.example.vetclinic.repository.TutorRepository;
import com.example.vetclinic.repository.VeterinarianRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Import(ApiTestSupport.FixedClockConfig.class)
abstract class ApiTestSupport {

    /** "Now" for every test: Monday 2030-01-14, 09:00. */
    static final LocalDateTime NOW = LocalDateTime.of(2030, 1, 14, 9, 0);

    private static final AtomicInteger SEQUENCE = new AtomicInteger();

    @TestConfiguration
    static class FixedClockConfig {

        @Bean
        @Primary
        Clock fixedClock() {
            ZoneId zone = ZoneId.of("America/Sao_Paulo");
            return Clock.fixed(NOW.atZone(zone).toInstant(), zone);
        }
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    PetRepository petRepository;

    @Autowired
    TutorRepository tutorRepository;

    @Autowired
    VeterinarianRepository veterinarianRepository;

    @BeforeEach
    void cleanDatabase() {
        appointmentRepository.deleteAll();
        petRepository.deleteAll();
        tutorRepository.deleteAll();
    }

    Tutor newTutor() {
        int n = SEQUENCE.incrementAndGet();
        return tutorRepository.save(new Tutor("Tutor " + n, "tutor" + n + "@example.com",
                String.format("%011d", n), "11999990000"));
    }

    Pet newPet() {
        return petRepository.save(new Pet("Thor", "DOG", 4, newTutor()));
    }

    Veterinarian generalVet() {
        return veterinarianBySpecialty("Clínica geral");
    }

    Veterinarian surgeon() {
        return veterinarianBySpecialty("Cirurgia");
    }

    private Veterinarian veterinarianBySpecialty(String specialty) {
        return veterinarianRepository.findAll().stream()
                .filter(v -> v.getSpecialty().equals(specialty))
                .findFirst()
                .orElseThrow();
    }
}

package com.example.vetclinic;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.example.vetclinic.domain.Pet;
import com.example.vetclinic.domain.Veterinarian;
import com.jayway.jsonpath.JsonPath;

class AppointmentApiTest extends ApiTestSupport {

    private static final LocalDateTime NEXT_WEEK_10H = LocalDateTime.of(2030, 1, 21, 10, 0);

    @Test
    void schedulesAppointmentChargingTheVeterinarianFee() throws Exception {
        Pet pet = newPet();

        schedule(pet, generalVet(), NEXT_WEEK_10H, false)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.petName").value("Thor"))
                .andExpect(jsonPath("$.veterinarianName").value("Dra. Helena Duarte"))
                .andExpect(jsonPath("$.status").value("SCHEDULED"))
                .andExpect(jsonPath("$.fee").value(80.0));
    }

    @Test
    void emergencyCostsFiftyPercentMoreAndIgnoresClinicHours() throws Exception {
        Pet pet = newPet();

        schedule(pet, generalVet(), NEXT_WEEK_10H.withHour(22), true)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.emergency").value(true))
                .andExpect(jsonPath("$.fee").value(120.0));
    }

    @Test
    void rejectsElectiveAppointmentOutsideClinicHours() throws Exception {
        Pet pet = newPet();

        schedule(pet, generalVet(), NEXT_WEEK_10H.withHour(19), false)
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void rejectsSecondAppointmentAtTheSameTime() throws Exception {
        Veterinarian vet = generalVet();

        schedule(newPet(), vet, NEXT_WEEK_10H, false).andExpect(status().isCreated());
        schedule(newPet(), vet, NEXT_WEEK_10H, false).andExpect(status().isConflict());
    }

    @Test
    void sameTimeWithAnotherVeterinarianIsAllowed() throws Exception {
        schedule(newPet(), generalVet(), NEXT_WEEK_10H, false).andExpect(status().isCreated());
        schedule(newPet(), surgeon(), NEXT_WEEK_10H, false).andExpect(status().isCreated());
    }

    @Test
    void timeSlotIsFreedAfterCancellation() throws Exception {
        Veterinarian vet = generalVet();
        long id = idOf(schedule(newPet(), vet, NEXT_WEEK_10H, false));

        mockMvc.perform(post("/appointments/{id}/cancel", id)).andExpect(status().isOk());

        schedule(newPet(), vet, NEXT_WEEK_10H, false).andExpect(status().isCreated());
    }

    @Test
    void cancellingWithMoreThan24hNoticeIsFree() throws Exception {
        long id = idOf(schedule(newPet(), generalVet(), NEXT_WEEK_10H, false));

        mockMvc.perform(post("/appointments/{id}/cancel", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.fee").value(0.0));
    }

    @Test
    void cancellingWithLessThan24hNoticeKeepsHalfTheFee() throws Exception {
        long id = idOf(schedule(newPet(), generalVet(), NOW.plusHours(6), false));

        mockMvc.perform(post("/appointments/{id}/cancel", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fee").value(40.0));
    }

    @Test
    void cannotCancelTwice() throws Exception {
        long id = idOf(schedule(newPet(), generalVet(), NEXT_WEEK_10H, false));

        mockMvc.perform(post("/appointments/{id}/cancel", id)).andExpect(status().isOk());
        mockMvc.perform(post("/appointments/{id}/cancel", id)).andExpect(status().isUnprocessableEntity());
    }

    @Test
    void completesScheduledAppointmentWithNotes() throws Exception {
        long id = idOf(schedule(newPet(), generalVet(), NEXT_WEEK_10H, false));

        mockMvc.perform(post("/appointments/{id}/complete", id).param("notes", "Vacina aplicada"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.notes").value("Vacina aplicada"))
                .andExpect(jsonPath("$.fee").value(80.0));
    }

    @Test
    void cannotCompleteTwice() throws Exception {
        long id = idOf(schedule(newPet(), generalVet(), NEXT_WEEK_10H, false));

        mockMvc.perform(post("/appointments/{id}/complete", id)).andExpect(status().isOk());
        mockMvc.perform(post("/appointments/{id}/complete", id)).andExpect(status().isUnprocessableEntity());
    }

    @Test
    void listsTheDayScheduleInChronologicalOrder() throws Exception {
        Veterinarian vet = generalVet();
        schedule(newPet(), vet, NEXT_WEEK_10H.withHour(14), false).andExpect(status().isCreated());
        schedule(newPet(), vet, NEXT_WEEK_10H.withHour(9), false).andExpect(status().isCreated());
        schedule(newPet(), vet, NEXT_WEEK_10H.plusDays(1), false).andExpect(status().isCreated());

        mockMvc.perform(get("/appointments")
                        .param("veterinarianId", vet.getId().toString())
                        .param("date", "2030-01-21"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].scheduledAt").value("2030-01-21T09:00:00"))
                .andExpect(jsonPath("$[1].scheduledAt").value("2030-01-21T14:00:00"));
    }

    @Test
    void returnsNotFoundForUnknownAppointment() throws Exception {
        mockMvc.perform(get("/appointments/{id}", 999999)).andExpect(status().isNotFound());
    }

    @Test
    void returnsNotFoundWhenPetDoesNotExist() throws Exception {
        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"petId\":999999,\"veterinarianId\":%d,\"scheduledAt\":\"2030-01-21T10:00:00\"}"
                                .formatted(generalVet().getId())))
                .andExpect(status().isNotFound());
    }

    private ResultActions schedule(Pet pet, Veterinarian vet, LocalDateTime when, boolean emergency)
            throws Exception {
        return mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"petId\":%d,\"veterinarianId\":%d,\"scheduledAt\":\"%s\",\"emergency\":%b}"
                        .formatted(pet.getId(), vet.getId(), when, emergency)));
    }

    private long idOf(ResultActions created) throws Exception {
        created.andExpect(status().isCreated());
        String body = created.andReturn().getResponse().getContentAsString();
        return JsonPath.parse(body).read("$.id", Long.class);
    }
}

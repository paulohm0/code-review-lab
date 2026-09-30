package com.example.vetclinic;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.example.vetclinic.domain.Tutor;

class TutorAndPetApiTest extends ApiTestSupport {

    private static final String TUTOR_JSON = """
            {"name":"Marina Lopes","email":"marina@example.com","document":"12345678901","phone":"11988887777"}
            """;

    @Test
    void createsTutor() throws Exception {
        mockMvc.perform(post("/tutors").contentType(MediaType.APPLICATION_JSON).content(TUTOR_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Marina Lopes"));
    }

    @Test
    void rejectsDuplicatedTutor() throws Exception {
        mockMvc.perform(post("/tutors").contentType(MediaType.APPLICATION_JSON).content(TUTOR_JSON))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/tutors").contentType(MediaType.APPLICATION_JSON).content(TUTOR_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void rejectsTutorWithInvalidEmail() throws Exception {
        mockMvc.perform(post("/tutors").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Ana\",\"email\":\"not-an-email\",\"document\":\"12345678901\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findsTutorById() throws Exception {
        Tutor tutor = newTutor();

        mockMvc.perform(get("/tutors/{id}", tutor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(tutor.getEmail()));
    }

    @Test
    void returnsNotFoundForUnknownTutor() throws Exception {
        mockMvc.perform(get("/tutors/{id}", 999999)).andExpect(status().isNotFound());
    }

    @Test
    void createsPetAndListsItByTutor() throws Exception {
        Tutor tutor = newTutor();

        mockMvc.perform(post("/pets").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tutorId\":%d,\"name\":\"Mel\",\"species\":\"CAT\",\"ageInYears\":2}"
                                .formatted(tutor.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tutorId").value(tutor.getId()));

        mockMvc.perform(get("/pets").param("tutorId", tutor.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Mel"));
    }

    @Test
    void rejectsPetWithoutName() throws Exception {
        Tutor tutor = newTutor();

        mockMvc.perform(post("/pets").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tutorId\":%d,\"name\":\"\",\"species\":\"CAT\"}".formatted(tutor.getId())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsPetForUnknownTutor() throws Exception {
        mockMvc.perform(post("/pets").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tutorId\":999999,\"name\":\"Mel\",\"species\":\"CAT\"}"))
                .andExpect(status().isNotFound());
    }
}

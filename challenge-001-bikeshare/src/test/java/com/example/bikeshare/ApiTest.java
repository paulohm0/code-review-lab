package com.example.bikeshare;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ApiTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void createsBike() throws Exception {
        mockMvc.perform(post("/bikes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"model\":\"Monark Barra Circular\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    void rejectsBikeWithoutModel() throws Exception {
        mockMvc.perform(post("/bikes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"model\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createsCustomerAndFindsItById() throws Exception {
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Bruno\",\"email\":\"bruno@example.com\",\"password\":\"senha1234\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Bruno"));
    }

    @Test
    void unknownRentalReturnsNotFound() throws Exception {
        mockMvc.perform(get("/rentals/9999"))
                .andExpect(status().isNotFound());
    }
}

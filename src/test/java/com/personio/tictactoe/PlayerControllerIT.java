package com.personio.tictactoe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personio.tictactoe.dto.PlayerRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PlayerControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;

    @Test
    void register_and_list_players() throws Exception {
        PlayerRegistrationRequest body = new PlayerRegistrationRequest("Eve");

        mockMvc.perform(post("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(body)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id", notNullValue()))
               .andExpect(jsonPath("$.name").value("Eve"));

        mockMvc.perform(get("/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", hasItem("Eve")));
    }
}
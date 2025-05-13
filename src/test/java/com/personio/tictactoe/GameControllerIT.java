package com.personio.tictactoe;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personio.tictactoe.dto.GameCreationRequest;
import com.personio.tictactoe.dto.PlayerRegistrationRequest;
import com.personio.tictactoe.dto.GameResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Scenario: Alice creates a game, Bob joins.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GameControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;
    private Long aliceId;
    private Long bobId;

    @BeforeEach
    void setUp() throws Exception {
        aliceId = registerPlayer("Alice");
        bobId   = registerPlayer("Bob");
    }

    @Test
    void create_and_join_game_flow() throws Exception {
        // Alice creates
        String gameJson = mockMvc.perform(post("/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(new GameCreationRequest(aliceId))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        GameResponse created = mapper.readValue(gameJson, new TypeReference<>() {});

        // Bob joins
        mockMvc.perform(post("/games/{id}/join", created.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"playerId\":" + bobId + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.opponentId").value(bobId));
    }

    private Long registerPlayer(String name) throws Exception {
        String res = mockMvc.perform(post("/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(new PlayerRegistrationRequest(name))))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return mapper.readTree(res).get("id").asLong();
    }
}
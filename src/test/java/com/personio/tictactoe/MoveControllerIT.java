package com.personio.tictactoe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personio.tictactoe.dto.GameCreationRequest;
import com.personio.tictactoe.dto.PlayerRegistrationRequest;
import com.personio.tictactoe.dto.PlayMoveRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Scenario: X plays first move at index 0
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MoveControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;

    private Long playerId;
    private Long gameId;

    private Long opponentId;   // ← new field

    @BeforeEach
    void setUp() throws Exception {
        playerId   = register("XPlayer");
        opponentId = register("OPlayer");                        // ← register second player
        gameId     = createGame(playerId);

        // Bob/O-player joins so the game can start (status PLAYER_X_TURN)
        mockMvc.perform(post("/games/{id}/join", gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerId\":" + opponentId + "}"))
                .andExpect(status().isOk());
    }

    @Test
    void play_first_move() throws Exception {
        PlayMoveRequest req = new PlayMoveRequest();
        req.setPlayerId(playerId);
        req.setMovementIndex(0);

        mockMvc.perform(post("/games/{gameId}/moves", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(req)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.board[0]").value("X"))
               .andExpect(jsonPath("$.gameStatus", notNullValue()));
    }

    private Long register(String name) throws Exception {
        String res = mockMvc.perform(post("/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(new PlayerRegistrationRequest(name))))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return mapper.readTree(res).path("id").asLong();           // ← safer access
    }

    private Long createGame(Long initiator) throws Exception {
        String res = mockMvc.perform(post("/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(new GameCreationRequest(initiator))))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return mapper.readTree(res).path("id").asLong();           // ← safer access
    }
}
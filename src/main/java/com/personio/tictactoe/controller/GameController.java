package com.personio.tictactoe.controller;

import com.personio.tictactoe.dto.*;
import com.personio.tictactoe.mapper.GameMapper;
import com.personio.tictactoe.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final GameMapper gameMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GameResponse createGame(@RequestBody @Valid GameCreationRequest request) {
        return gameMapper.toResponse(gameService.createGame(request.initiatorId()));
    }

    @PostMapping("/ai")
    @ResponseStatus(HttpStatus.CREATED)
    public GameResponse playVsAi(@RequestBody @Valid GameCreationRequest request, @RequestParam(defaultValue = "true") boolean playsFirst) {
        return gameMapper.toResponse(gameService.createGameVsAi(request.initiatorId(), playsFirst));
    }

    @PostMapping("/ai/moves")
    public AiMoveResponse moveVsAi(@RequestBody @Valid AiMoveRequest request) {
        return gameService.playMoveVsAi(request);
    }

    @GetMapping("/available")
    public List<GameResponse> listAvailableGames() {
        return gameMapper.toResponse(gameService.findAvailableGames());
    }

    @PostMapping("/{id}/join")
    public GameResponse join(@PathVariable Long id, @RequestBody JoinGameRequest request) {
        return gameMapper.toResponse(gameService.joinGame(id, request.playerId()));
    }
}
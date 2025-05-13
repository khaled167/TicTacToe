package com.personio.tictactoe.controller;

import com.personio.tictactoe.dto.PlayerRegistrationRequest;
import com.personio.tictactoe.dto.PlayerResponse;
import com.personio.tictactoe.mapper.PlayerMapper;
import com.personio.tictactoe.model.Player;
import com.personio.tictactoe.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;
    private final PlayerMapper playerMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlayerResponse registerPlayer(@RequestBody @Valid PlayerRegistrationRequest request) {
        return playerMapper.toResponse(playerService.registerPlayer(request.name()));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Player> getAllPlayers() {
        return playerService.getPlayers();
    }

}
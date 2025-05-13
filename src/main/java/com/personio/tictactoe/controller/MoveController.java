package com.personio.tictactoe.controller;

import com.personio.tictactoe.dto.PlayMoveRequest;
import com.personio.tictactoe.dto.PlayMoveResponse;
import com.personio.tictactoe.service.MoveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class MoveController {

    private final MoveService moveService;

    @PostMapping("{gameId}/moves")
    @ResponseStatus(HttpStatus.OK)
    public PlayMoveResponse playMove(@PathVariable Long gameId, @Valid @RequestBody PlayMoveRequest request) {
        return moveService.playMove(gameId, request);
    }

    @GetMapping("/{id}")
    public PlayMoveResponse getGame(@PathVariable Long id) {
        return moveService.viewGame(id);
    }

}
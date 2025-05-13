package com.personio.tictactoe.dto;

import com.personio.tictactoe.model.enums.GameStatus;

import java.util.List;

public record AiMoveResponse(
        List<String> board,
        String row1,
        String row2,
        String row3,
        GameStatus gameStatus,
        String winner,
        int aiMoveIndex
) {
}
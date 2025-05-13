package com.personio.tictactoe.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AiMoveRequest(
        @NotNull Long gameId,
        @NotNull Long playerId,
        @NotNull @Min(0) @Max(8) Integer movementIndex
) {
}
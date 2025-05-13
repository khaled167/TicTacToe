package com.personio.tictactoe.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlayMoveRequest {

    @NotNull
    private Long playerId;

    @Min(0)
    @Max(8)
    private Integer movementIndex;
}
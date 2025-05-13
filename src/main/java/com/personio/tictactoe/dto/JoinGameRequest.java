package com.personio.tictactoe.dto;

import jakarta.validation.constraints.NotNull;

public record JoinGameRequest(@NotNull Long playerId) {}
package com.personio.tictactoe.dto;

import jakarta.validation.constraints.NotNull;

public record GameCreationRequest(@NotNull Long initiatorId) {}
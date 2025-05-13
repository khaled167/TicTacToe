package com.personio.tictactoe.dto;

import jakarta.validation.constraints.NotBlank;

public record PlayerRegistrationRequest(@NotBlank String name) {}
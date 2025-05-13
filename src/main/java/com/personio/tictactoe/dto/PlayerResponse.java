package com.personio.tictactoe.dto;

public record PlayerResponse(Long id, String name, Integer totalGames, Integer wins, Integer loss, Integer draws) {}
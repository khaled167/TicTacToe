package com.personio.tictactoe.dto;

import com.personio.tictactoe.model.enums.GameStatus;

import java.util.Date;

public record GameResponse(Long id, GameStatus status, Date creationDate, Long initiatorId, Long opponentId) {}
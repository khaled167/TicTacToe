package com.personio.tictactoe.exception;

public class PlayerAlreadyInGameException extends RuntimeException {
    public PlayerAlreadyInGameException(Long playerId) {
        super("Player " + playerId + " is already participating in an active game");
    }
}
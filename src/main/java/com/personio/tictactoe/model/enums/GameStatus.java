package com.personio.tictactoe.model.enums;

import lombok.Getter;

@Getter
public enum GameStatus {

    WAITING_FOR_OPPONENT,
    PLAYER_X_TURN,
    PLAYER_O_TURN,
    X_WON,
    O_WON,
    DRAW
}

package com.personio.tictactoe.dto;

import com.personio.tictactoe.model.enums.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PlayMoveResponse {

    private List<String> board;

    private String row1;
    private String row2;
    private String row3;

    private GameStatus gameStatus;

    private String winner;
}
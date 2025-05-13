package com.personio.tictactoe.util;

import com.personio.tictactoe.model.enums.Movement;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class BoardEvaluator {

    private static final int[][] WIN_LINES = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}
    };

    private BoardEvaluator() {
    }

    public static Optional<Movement> winner(List<Movement> board) {
        return Arrays.stream(WIN_LINES)
                .map(line -> board.get(line[0]))
                .filter(Objects::nonNull)
                .filter(m -> Arrays.stream(WIN_LINES).anyMatch(line -> m == board.get(line[0])
                                && m == board.get(line[1])
                                && m == board.get(line[2])))
                .findFirst();
    }

    public static boolean isDraw(List<Movement> board) {
        return board.stream().allMatch(Objects::nonNull) && winner(board).isEmpty();
    }
}
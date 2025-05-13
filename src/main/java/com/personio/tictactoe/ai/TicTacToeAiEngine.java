package com.personio.tictactoe.ai;

public class TicTacToeAiEngine {

    public int computeBestMove(String board, char maxSymbol) {
        if (board == null || board.length() != 9)
            throw new IllegalArgumentException("board must be 9 chars");

        char min = (maxSymbol == 'X') ? 'O' : 'X';
        char[] cells = board.toCharArray();

        if (isWinner(cells, maxSymbol) || isWinner(cells, min) || isFull(cells))
            return -1;

        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;

        for (int i = 0; i < 9; i++) {
            if (cells[i] != '_') continue;

            cells[i] = maxSymbol;
            int score = minimax(cells, false, maxSymbol, min);
            cells[i] = '_';

            if (score > bestScore) {
                bestScore = score;
                bestMove = i;
            }
        }
        return bestMove;
    }

    private int minimax(char[] cells, boolean isAiTurn, char max, char min) {
        if (isWinner(cells, max)) return +1000_000_000;
        if (isWinner(cells, min)) return -1000_000_000;
        if (isFull(cells)) return 0;

        char current = isAiTurn ? max : min;
        int best = isAiTurn ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i < 9; i++) {
            if (cells[i] != '_') continue;

            cells[i] = current;
            int score = minimax(cells, !isAiTurn, max, min);
            cells[i] = '_';

            if (isAiTurn) best = Math.max(best, score);
            else best = Math.min(best, score);
        }
        return best;
    }

    private boolean isFull(char[] cells) {
        for (char c : cells) if (c == '_') return false;
        return true;
    }

    private boolean isWinner(char[] c, char p) {
        int[][] lines = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // rows
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // cols
                {0, 4, 8}, {2, 4, 6}          // diags
        };
        for (int[] line : lines) {
            if (c[line[0]] == p && c[line[1]] == p && c[line[2]] == p) return true;
        }
        return false;
    }
}
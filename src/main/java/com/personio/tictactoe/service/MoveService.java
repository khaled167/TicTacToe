package com.personio.tictactoe.service;

import com.personio.tictactoe.dto.PlayMoveRequest;
import com.personio.tictactoe.dto.PlayMoveResponse;
import com.personio.tictactoe.exception.ConflictException;
import com.personio.tictactoe.exception.ForbiddenException;
import com.personio.tictactoe.exception.NotFoundException;
import com.personio.tictactoe.model.Game;
import com.personio.tictactoe.model.Move;
import com.personio.tictactoe.model.Player;
import com.personio.tictactoe.model.enums.GameStatus;
import com.personio.tictactoe.model.enums.Movement;
import com.personio.tictactoe.repository.GameRepository;
import com.personio.tictactoe.repository.MoveRepository;
import com.personio.tictactoe.util.BoardEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MoveService {

    private final GameRepository gameRepo;
    private final MoveRepository moveRepo;

    public PlayMoveResponse playMove(Long gameId, PlayMoveRequest req) {

        Game game = gameRepo.findByIdForUpdate(gameId).orElseThrow(() -> new NotFoundException("Game not found"));

        Player player = resolvePlayer(game, req.getPlayerId());
        Movement playerSymbol = getPlayerSymbol(game, player);

        validateTurn(game, playerSymbol);

        if (moveRepo.existsByGameIdAndMovementIndex(gameId, req.getMovementIndex()))
            throw new ConflictException("Cell already taken");

        Move move = new Move().setGame(game).setPlayer(player).setMovementIndex(req.getMovementIndex()).setMovement(playerSymbol);

        try { moveRepo.saveAndFlush(move); }
        catch (DataIntegrityViolationException ignore) {}

        List<Movement> board = buildBoard(gameId);
        GameStatus newStatus = evaluateAndUpdateStatus(game, board);

        List<String> boardStrings = boardToString(board);
        List<String> rows = toRowStrings(boardStrings);

        return new PlayMoveResponse(
                boardStrings,
                rows.get(0), rows.get(1), rows.get(2),
                newStatus,
                winnerName(game, newStatus)
        );
    }

    private Player resolvePlayer(Game game, Long playerId) {

        if (game.getInitiator().getId().equals(playerId))
            return game.getInitiator();

        if (game.getOpponent() != null && game.getOpponent().getId().equals(playerId))
            return game.getOpponent();

        throw new ForbiddenException("Player doesn't belong to this game");
    }

    private Movement getPlayerSymbol(Game game, Player player) {
        return game.getInitiator().equals(player) ? Movement.X : Movement.O;
    }

    private void validateTurn(Game game, Movement playerSymbol) {

        if (game.getGameStatus() == GameStatus.X_WON || game.getGameStatus() == GameStatus.O_WON)
            throw new ConflictException("Game already finished");

        if (game.getGameStatus() == GameStatus.WAITING_FOR_OPPONENT)
            throw new ConflictException("Opponent not joined yet");

        boolean xTurn = game.getGameStatus() == GameStatus.PLAYER_X_TURN;

        if (xTurn && playerSymbol != Movement.X)
            throw new ForbiddenException("It's X player's turn");

        if (!xTurn && playerSymbol != Movement.O)
            throw new ForbiddenException("It's O player's turn");

    }

    private List<Movement> buildBoard(Long gameId) {
        List<Movement> board = new ArrayList<>(Arrays.asList(new Movement[9]));
        moveRepo.findByGameId(gameId).forEach(m -> board.set(m.getMovementIndex(), m.getMovement()));
        return board;
    }

    private GameStatus evaluateAndUpdateStatus(Game game, List<Movement> board) {

        var winnerOpt = BoardEvaluator.winner(board);

        GameStatus newStatus;

        if (winnerOpt.isPresent())
            newStatus = winnerOpt.get() == Movement.X ? GameStatus.X_WON : GameStatus.O_WON;

        else if (BoardEvaluator.isDraw(board))
            newStatus = GameStatus.DRAW;

        else
            newStatus = (game.getGameStatus() == GameStatus.PLAYER_X_TURN) ? GameStatus.PLAYER_O_TURN : GameStatus.PLAYER_X_TURN;

        game.setGameStatus(newStatus);

        return newStatus;
    }

    private List<String> boardToString(List<Movement> board) {
        List<String> out = new ArrayList<>(9);
        board.forEach(m -> out.add(m == null ? null : m.name()));
        return out;
    }

    private String val(String s) {
        return (s == null || "_".equals(s)) ? " " : s;
    }

    private List<String> toRowStrings(List<String> board) {
        return List.of(
                formatRow(board.get(0), board.get(1), board.get(2)),
                formatRow(board.get(3), board.get(4), board.get(5)),
                formatRow(board.get(6), board.get(7), board.get(8))
        );
    }

    private String formatRow(String a, String b, String c) {
        return "%s | %s | %s".formatted(val(a), val(b), val(c));
    }

    private String winnerName(Game game, GameStatus status) {
        return switch (status) {
            case X_WON -> game.getInitiator().getName();
            case O_WON -> game.getOpponent().getName();
            default -> null;
        };
    }

    public PlayMoveResponse viewGame(Long gameId) {
        Game game = gameRepo.findById(gameId)
                .orElseThrow(() -> new NotFoundException("Game not found"));

        List<Movement> board = buildBoard(gameId);

        List<String> boardStrings = boardToString(board);
        List<String> rows = toRowStrings(boardStrings);

        return new PlayMoveResponse(
                boardStrings,
                rows.get(0), rows.get(1), rows.get(2),
                game.getGameStatus(),
                winnerName(game, game.getGameStatus())
        );
    }

}
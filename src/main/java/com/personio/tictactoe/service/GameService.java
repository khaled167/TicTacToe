package com.personio.tictactoe.service;

import com.personio.tictactoe.ai.TicTacToeAiEngine;
import com.personio.tictactoe.dto.AiMoveRequest;
import com.personio.tictactoe.dto.AiMoveResponse;
import com.personio.tictactoe.dto.PlayMoveRequest;
import com.personio.tictactoe.dto.PlayMoveResponse;
import com.personio.tictactoe.exception.PlayerAlreadyInGameException;
import com.personio.tictactoe.model.Game;
import com.personio.tictactoe.model.Player;
import com.personio.tictactoe.model.enums.GameStatus;
import com.personio.tictactoe.repository.GameRepository;
import com.personio.tictactoe.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GameService {

    private static final List<GameStatus> ACTIVE_STATUSES = List.of(
            GameStatus.WAITING_FOR_OPPONENT,
            GameStatus.PLAYER_X_TURN,
            GameStatus.PLAYER_O_TURN
    );

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final MoveService moveService;

    private final TicTacToeAiEngine engine = new TicTacToeAiEngine();


    public Game createGame(Long initiatorId) {
        if (gameRepository.existsActiveGameForPlayer(initiatorId, ACTIVE_STATUSES))
            throw new PlayerAlreadyInGameException(initiatorId);

        Player initiator = playerRepository.findById(initiatorId)
                .orElseThrow(() -> new IllegalArgumentException("Player with id %d not found".formatted(initiatorId)));

        Game game = new Game()
                .setGameStatus(GameStatus.WAITING_FOR_OPPONENT)
                .setInitiator(initiator);

        return gameRepository.save(game);
    }

    @Transactional(readOnly = true)
    public List<Game> findAvailableGames() {
        return gameRepository.findAllByGameStatus(GameStatus.WAITING_FOR_OPPONENT);
    }

    public Game joinGame(Long gameId, Long playerId) {

        if (gameRepository.existsActiveGameForPlayer(playerId, ACTIVE_STATUSES))
            throw new PlayerAlreadyInGameException(playerId);

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game %d not found".formatted(gameId)));

        validateGameJoining(game, gameId, playerId);

        Player opponent = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player %d not found".formatted(playerId)));

        game.setOpponent(opponent)
                .setGameStatus(GameStatus.PLAYER_X_TURN);

        return gameRepository.save(game);
    }


    public Game createGameVsAi(Long humanId, boolean humanPlaysFirst) {

        if (gameRepository.existsActiveGameForPlayer(humanId, ACTIVE_STATUSES))
            throw new PlayerAlreadyInGameException(humanId);

        Player human = playerRepository.findById(humanId)
                .orElseThrow(() -> new IllegalArgumentException("Player with id %d not found".formatted(humanId)));

        Player ai = getOrCreateAiPlayer();

        boolean aiPlaysFirst = !humanPlaysFirst;

        Game game;
        if (humanPlaysFirst) {
            game = new Game()
                    .setInitiator(human)
                    .setOpponent(ai)
                    .setGameStatus(GameStatus.PLAYER_X_TURN);
        } else {
            game = new Game()
                    .setInitiator(ai)
                    .setOpponent(human)
                    .setGameStatus(GameStatus.PLAYER_X_TURN);
        }

        gameRepository.save(game);

        if (aiPlaysFirst) {
            int aiMove = computeAiMoveIndex(Collections.nCopies(9, null), 'X');
            if (aiMove != -1) {
                PlayMoveRequest aiReq = new PlayMoveRequest();
                aiReq.setPlayerId(ai.getId());
                aiReq.setMovementIndex(aiMove);
                moveService.playMove(game.getId(), aiReq);
            }
        }

        return gameRepository.findById(game.getId()).orElseThrow();
    }

    public AiMoveResponse playMoveVsAi(AiMoveRequest req) {

        PlayMoveRequest humanReq = new PlayMoveRequest();
        humanReq.setPlayerId(req.playerId());
        humanReq.setMovementIndex(req.movementIndex());

        PlayMoveResponse afterHuman = moveService.playMove(req.gameId(), humanReq);

        if (!isGameRunning(afterHuman.getGameStatus()))
            return toAiMoveResponse(afterHuman, -1);

        Game game = gameRepository.findById(req.gameId()).orElseThrow();
        char aiSymbol = getAiSymbol(game);

        int aiMove = computeAiMoveIndex(afterHuman.getBoard(), aiSymbol);
        if (aiMove == -1)
            return toAiMoveResponse(afterHuman, -1);

        PlayMoveRequest aiReq = new PlayMoveRequest();
        aiReq.setPlayerId(getOrCreateAiPlayer().getId());
        aiReq.setMovementIndex(aiMove);

        PlayMoveResponse afterAi = moveService.playMove(req.gameId(), aiReq);

        return toAiMoveResponse(afterAi, aiMove);
    }

    private boolean isGameRunning(GameStatus status) {
        return status == GameStatus.PLAYER_X_TURN || status == GameStatus.PLAYER_O_TURN;
    }

    private AiMoveResponse toAiMoveResponse(PlayMoveResponse p, int aiIdx) {
        return new AiMoveResponse(
                p.getBoard(),
                p.getRow1(),
                p.getRow2(),
                p.getRow3(),
                p.getGameStatus(),
                p.getWinner(),
                aiIdx
        );
    }

    private Player getOrCreateAiPlayer() {
        return playerRepository.findPlayerByName("AI_BOT").orElseGet(() -> playerRepository.save(new Player().setName("AI_BOT")));
    }

    private int computeAiMoveIndex(List<String> board, char aiSymbol) {
        String flat = board.stream()
                .map(s -> s == null ? "_" : s)
                .collect(Collectors.joining());
        return engine.computeBestMove(flat, aiSymbol);
    }

    private char getAiSymbol(Game game) {
        return game.getInitiator().getName().equals("AI_BOT") ? 'X' : 'O';
    }

    private void validateGameJoining(Game game, Long gameId, Long playerId) {
        if (game.getOpponent() != null)
            throw new IllegalStateException("Game %d already has an opponent".formatted(gameId));

        if (!GameStatus.WAITING_FOR_OPPONENT.equals(game.getGameStatus()))
            throw new IllegalStateException("Game %d is not open for joining".formatted(gameId));

        if (game.getInitiator().getId().equals(playerId))
            throw new IllegalArgumentException("Initiator cannot join their own game as opponent");
    }
}
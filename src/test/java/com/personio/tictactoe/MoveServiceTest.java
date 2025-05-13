package com.personio.tictactoe;

import com.personio.tictactoe.dto.PlayMoveRequest;
import com.personio.tictactoe.dto.PlayMoveResponse;
import com.personio.tictactoe.model.Game;
import com.personio.tictactoe.model.Move;
import com.personio.tictactoe.model.Player;
import com.personio.tictactoe.model.enums.GameStatus;
import com.personio.tictactoe.model.enums.Movement;
import com.personio.tictactoe.repository.GameRepository;
import com.personio.tictactoe.repository.MoveRepository;
import com.personio.tictactoe.service.MoveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoveServiceTest {

    @Mock  private MoveRepository  moveRepository;
    @Mock  private GameRepository  gameRepository;
    @InjectMocks private MoveService   moveService;

    private Player alice;
    private Game   game;

    @BeforeEach
    void setUp() {
        alice = new Player().setId(1L).setName("Alice");

        game = new Game()
                .setId(100L)
                .setInitiator(alice)
                .setOpponent(new Player().setId(2L).setName("Bob"))
                .setGameStatus(GameStatus.PLAYER_X_TURN);
    }

    @Test
    void playMove_places_token_and_returns_board() {
        /* ── Arrange ─────────────────────────────────────────────────────────── */
        when(gameRepository.findByIdForUpdate(100L))
                .thenReturn(Optional.of(game));

        int cell = 0;
        when(moveRepository.existsByGameIdAndMovementIndex(100L, cell))
                .thenReturn(false);

        // the move the service should store
        Move persisted = new Move()
                .setId(50L)
                .setGame(game)
                .setPlayer(alice)
                .setMovementIndex(cell)
                .setMovement(Movement.X);

        when(moveRepository.saveAndFlush(any(Move.class))).thenReturn(persisted);
        when(moveRepository.findByGameId(100L)).thenReturn(List.of(persisted));

        PlayMoveRequest request = new PlayMoveRequest()
                .setPlayerId(alice.getId())
                .setMovementIndex(cell);

        /* ── Act ─────────────────────────────────────────────────────────────── */
        PlayMoveResponse response = moveService.playMove(100L, request);

        /* ── Assert ──────────────────────────────────────────────────────────── */
        assertThat(response.getBoard()).hasSize(9);
        assertThat(response.getBoard().get(cell)).isEqualTo(Movement.X.name());

        ArgumentCaptor<Move> saved = ArgumentCaptor.forClass(Move.class);
        verify(moveRepository).saveAndFlush(saved.capture());
        assertThat(saved.getValue().getMovementIndex()).isEqualTo(cell);
        assertThat(saved.getValue().getMovement()).isEqualTo(Movement.X);
    }
}
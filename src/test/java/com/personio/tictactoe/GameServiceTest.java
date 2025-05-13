package com.personio.tictactoe;

import com.personio.tictactoe.model.Game;
import com.personio.tictactoe.model.Player;
import com.personio.tictactoe.model.enums.GameStatus;
import com.personio.tictactoe.repository.GameRepository;
import com.personio.tictactoe.repository.PlayerRepository;
import com.personio.tictactoe.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock private GameRepository gameRepository;
    @Mock private PlayerRepository playerRepository;
    @InjectMocks private GameService gameService;

    private Player initiator;

    @BeforeEach
    void init() {
        initiator = new Player().setId(5L).setName("Bob");
    }

    @Test
    void createGame_stores_game_waiting_for_opponent() {
        when(playerRepository.findById(5L)).thenReturn(java.util.Optional.of(initiator));

        Game stored = new Game()
                .setId(9L)
                .setInitiator(initiator)
                .setGameStatus(GameStatus.WAITING_FOR_OPPONENT);
        when(gameRepository.save(any(Game.class))).thenReturn(stored);

        Game result = gameService.createGame(5L);

        assertThat(result.getId()).isEqualTo(9L);
        assertThat(result.getGameStatus()).isEqualTo(GameStatus.WAITING_FOR_OPPONENT);
    }
}
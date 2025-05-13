package com.personio.tictactoe;

import com.personio.tictactoe.model.Player;
import com.personio.tictactoe.repository.PlayerRepository;
import com.personio.tictactoe.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    private Player persisted;

    @BeforeEach
    void setUp() {
        persisted = new Player()
                .setId(1L)
                .setName("Alice")
                .setTotalGames(0)
                .setWins(0)
                .setLoss(0)
                .setDraws(0);
    }

    @Test
    void registerPlayer_persists_and_returns_entity() {
        when(playerRepository.save(any(Player.class))).thenReturn(persisted);

        Player result = playerService.registerPlayer("Alice");

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Alice");

        ArgumentCaptor<Player> saved = ArgumentCaptor.forClass(Player.class);
        verify(playerRepository).save(saved.capture());
        assertThat(saved.getValue().getName()).isEqualTo("Alice");
    }

    @Test
    void getPlayers_returns_all_records() {
        when(playerRepository.findAll()).thenReturn(List.of(persisted));

        List<Player> players = playerService.getPlayers();

        assertThat(players).hasSize(1).first()
                .satisfies(p -> assertThat(p.getName()).isEqualTo("Alice"));
    }
}
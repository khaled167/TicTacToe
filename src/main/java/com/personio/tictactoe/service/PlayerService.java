package com.personio.tictactoe.service;

import com.personio.tictactoe.model.Player;
import com.personio.tictactoe.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PlayerService {

    private final PlayerRepository playerRepository;

    public Player registerPlayer(String name) {
        Player player = new Player().setName(name).setTotalGames(0).setWins(0).setLoss(0).setDraws(0);
        return playerRepository.save(player);
    }

    public List<Player> getPlayers() {
        return playerRepository.findAll();
    }
}
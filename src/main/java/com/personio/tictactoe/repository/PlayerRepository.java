package com.personio.tictactoe.repository;

import com.personio.tictactoe.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    Optional<Player> findPlayerByName(String name);
}
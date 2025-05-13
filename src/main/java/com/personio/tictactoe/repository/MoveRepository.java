package com.personio.tictactoe.repository;

import com.personio.tictactoe.model.Move;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MoveRepository extends JpaRepository<Move, Long> {

    boolean existsByGameIdAndMovementIndex(Long gameId, Integer movementIndex);

    List<Move> findByGameId(Long gameId);
}
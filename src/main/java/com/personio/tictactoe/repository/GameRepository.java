package com.personio.tictactoe.repository;

import com.personio.tictactoe.model.Game;
import com.personio.tictactoe.model.enums.GameStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select g from Game g where g.id = :id")
    Optional<Game> findByIdForUpdate(@Param("id") Long id);

    List<Game> findAllByGameStatus(GameStatus gameStatus);

    @Query("""
            SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END
            FROM Game g
            WHERE (g.initiator.id = :playerId OR g.opponent.id = :playerId)
              AND g.gameStatus IN :activeStatuses
            """)
    boolean existsActiveGameForPlayer(@Param("playerId") Long playerId,
                                      @Param("activeStatuses") List<GameStatus> activeStatuses);

}
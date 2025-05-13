package com.personio.tictactoe.model;

import com.personio.tictactoe.model.enums.Movement;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Table(name = "moves",
        uniqueConstraints = @UniqueConstraint(name = "uk_move_player_game_idx", columnNames = {"player_id", "game_id", "movement_index"})
)
@Entity
@Accessors(chain = true)
@Getter
@Setter
public class Move {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "movement_index", nullable = false)
    private Integer movementIndex;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Movement movement;
}
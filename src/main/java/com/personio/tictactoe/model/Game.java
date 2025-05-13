package com.personio.tictactoe.model;

import com.personio.tictactoe.model.enums.GameStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "games", uniqueConstraints = @UniqueConstraint(name = "uk_game_opponent_id", columnNames = {"opponent_id"}))
@Accessors(chain = true)
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameStatus gameStatus;

    @Column
    private Date creationDate = new Date();

    @ManyToOne(optional = false)
    private Player initiator;

    @ManyToOne
    private Player opponent;

    @Version
    private Long version;
}
package com.personio.tictactoe.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Table(name = "players")
@Entity
@Getter
@Setter
@Accessors(chain = true)
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private Integer totalGames;

    @Column
    private Integer wins;

    @Column
    private Integer loss;

    @Column
    private Integer draws;
}

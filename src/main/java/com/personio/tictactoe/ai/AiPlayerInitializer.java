package com.personio.tictactoe.ai;

import com.personio.tictactoe.model.Player;
import com.personio.tictactoe.repository.PlayerRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AiPlayerInitializer {

    public static final String AI_NAME = "AI_BOT";

    private final PlayerRepository playerRepository;

    @PostConstruct
    void ensureAiPlayerExists() {
        playerRepository.findAll()
                        .stream()
                        .filter(p -> AI_NAME.equals(p.getName()))
                        .findFirst()
                        .orElseGet(() -> playerRepository.save(new Player().setName(AI_NAME)));
    }
}
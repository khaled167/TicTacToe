package com.personio.tictactoe.mapper;

import com.personio.tictactoe.dto.GameResponse;
import com.personio.tictactoe.model.Game;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GameMapper {

    @Mapping(source = "gameStatus", target = "status")
    @Mapping(source = "initiator.id", target = "initiatorId")
    @Mapping(source = "opponent.id", target = "opponentId")
    GameResponse toResponse(Game game);

    List<GameResponse> toResponse(List<Game> games);
}
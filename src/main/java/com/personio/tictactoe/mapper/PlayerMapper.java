package com.personio.tictactoe.mapper;

import com.personio.tictactoe.dto.PlayerResponse;
import com.personio.tictactoe.model.Player;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlayerMapper {

    PlayerResponse toResponse(Player player);
}
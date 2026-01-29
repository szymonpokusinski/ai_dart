package com.aidart.backend.game.controller;

import com.aidart.backend.game.Game;
import com.aidart.backend.game.dto.*;
import com.aidart.backend.game.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    @PostMapping
    public ResponseEntity<GameDto> create(@Valid @RequestBody GameRequest request) {
        CreateGameCommand command = new CreateGameCommand(
                request.playersIds(),
                request.type(),
                request.finishRule()
        );

        Game savedGame = gameService.create(command);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(GameDto.fromEntity(savedGame));
    }

}

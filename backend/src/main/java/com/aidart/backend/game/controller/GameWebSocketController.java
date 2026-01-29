package com.aidart.backend.game.controller;

import com.aidart.backend.game.Game;
import com.aidart.backend.game.GameState;
import com.aidart.backend.game.dto.CreateGameCommand;
import com.aidart.backend.game.dto.GameInitializedEvent;
import com.aidart.backend.game.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GameWebSocketController {

    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/game/{uuid}/join")
    public void joinGame(@DestinationVariable("uuid") String gameUuidStr, Principal principal) {
        if (principal == null) {
            return;
        }

        try {
            UUID.fromString(gameUuidStr);
        } catch (IllegalArgumentException e) {
            sendError(principal.getName(), "Niepoprawny format identyfikatora gry!");
            return;
        }

        GameState currentState = gameService.getGameState(gameUuidStr);

        if (currentState == null) {
            sendError(principal.getName(), "Gra o podanym UUID nie istnieje!");
            return;
        }

        messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/game-state",
                currentState
        );
    }

    private void sendError(String username, String message) {
        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/errors",
                message
        );
    }

    @MessageMapping("/throw/{uuid}/throw")
    public void handleThrow(){

    }

    @MessageMapping("/throw/{uuid}/delete")
    public void handleThrowDelete(){

    }

}

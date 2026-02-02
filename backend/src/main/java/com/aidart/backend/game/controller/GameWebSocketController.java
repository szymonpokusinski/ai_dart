package com.aidart.backend.game.controller;

import com.aidart.backend.game.GameState;
import com.aidart.backend.game.dto.GameDto;
import com.aidart.backend.game.dto.GameFinalizedResponse;
import com.aidart.backend.game.service.GameService;
import com.aidart.backend.shot.ThrowRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
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
        if (principal == null) return;

        UUID gameUuid;
        try {
            gameUuid = UUID.fromString(gameUuidStr);
        } catch (IllegalArgumentException e) {
            sendError(principal.getName(), "Niepoprawny format identyfikatora gry!");
            return;
        }

        GameState currentState = gameService.getGameState(gameUuid.toString());
        if (currentState == null) {
            sendError(principal.getName(), "Gra o podanym UUID nie istnieje!");
            return;
        }

        messagingTemplate.convertAndSend(
                "/topic/game/" + gameUuid + "/state",
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
    public void handleThrow(
            @DestinationVariable("uuid") String gameUuid,
            @Payload ThrowRequest request,
            Principal principal
    ) {
        if (principal == null) return;

        try {
            GameState updatedState = gameService.processThrow(gameUuid, request);

            messagingTemplate.convertAndSend(
                    "/topic/game/" + gameUuid + "/state",
                    updatedState
            );
        } catch (Exception e) {
            sendError(principal.getName(), e.getMessage());
        }
    }

    @MessageMapping("/throw/{uuid}/delete")
    public void handleThrowDelete(
            @DestinationVariable("uuid") String gameUuid,
            Principal principal
    ) {
        try {
            GameState updatedStare = gameService.deleteShot(gameUuid);

            messagingTemplate.convertAndSend(
                    "/topic/game/" + gameUuid + "/state",
                    updatedStare
            );
        } catch (Exception e) {
            sendError(principal.getName(), e.getMessage());
        }
    }

    @MessageMapping("game/{uuid}/finalize")
    public void finalize(
            @DestinationVariable("uuid") String gameUuid,
            Principal principal
    ) {
        try {
            GameDto gameDto = gameService.finalizeGame(gameUuid);
            messagingTemplate.convertAndSend(
                    "/topic/game/" + gameUuid + "/state",
                    new GameFinalizedResponse(gameUuid, gameDto.id(), "FINISHED")
                    );
        } catch (Exception e) {
            sendError(principal.getName(), e.getMessage());
        }
    }

}

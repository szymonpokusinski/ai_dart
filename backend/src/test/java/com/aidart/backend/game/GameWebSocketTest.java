package com.aidart.backend.game;

import com.aidart.backend.game.dto.CreateGameCommand;
import com.aidart.backend.game.dto.GameInitializedEvent;
import com.aidart.backend.game.dto.PlayerOrder;
import com.aidart.backend.game.enums.GameFinishRule;
import com.aidart.backend.game.enums.GameType;
import com.aidart.backend.game.service.GameService;
import com.aidart.backend.player.Player;
import com.aidart.backend.player.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                // Baza H2
                "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
                "spring.datasource.driverClassName=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.flyway.enabled=false",
                // LOGOWANIE - KLUCZOWE DO DIAGNOZY
                "logging.level.org.springframework.web.socket=DEBUG",
                "logging.level.org.springframework.messaging=DEBUG"
        }
)
public class GameWebSocketTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private PlayerRepository playerRepository;

    private WebSocketStompClient stompClient;

    private Long player1Id;
    private Long player2Id;

    @BeforeEach
    void setUp() {
        // Używamy StandardWebSocketClient bezpośrednio (bez SockJS) dla prostoty w testach
        this.stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        // Czyszczenie i przygotowanie danych
        playerRepository.deleteAll();

        Player p1 = new Player();
        p1.setName("SzymonTest");
        // Ustaw inne wymagane pola (np. email) jeśli encja tego wymaga!
        // p1.setEmail("test1@test.com");
        Player savedP1 = playerRepository.save(p1);
        this.player1Id = savedP1.getId();

        Player p2 = new Player();
        p2.setName("BotTest");
        // p2.setEmail("test2@test.com");
        Player savedP2 = playerRepository.save(p2);
        this.player2Id = savedP2.getId();
    }

    @Test
    void shouldInitGameAndReceiveId() throws ExecutionException, InterruptedException, TimeoutException {
        // UWAGA: Dodajemy "/websocket" na końcu, bo omijamy SockJS i łączymy się bezpośrednio
        String url = String.format("ws://localhost:%d/ws-aidart/websocket", port);

        CompletableFuture<GameInitializedEvent> completableFuture = new CompletableFuture<>();

        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                System.out.println(">>> Połączono z WS! Sesja ID: " + session.getSessionId());

                session.subscribe("/user/queue/game-started", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return GameInitializedEvent.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        System.out.println(">>> Otrzymano odpowiedź!");
                        completableFuture.complete((GameInitializedEvent) payload);
                    }
                });
                CreateGameCommand command = new CreateGameCommand(
                        List.of(
                                new PlayerOrder(player1Id, 1),
                                new PlayerOrder(player2Id, 2)
                        ),
                        GameType.TYPE_501, // Sprawdź czy to na pewno dobra nazwa enuma
                        GameFinishRule.DOUBLE_OUT
                );

                System.out.println(">>> Wysyłanie komendy: " + command);
                session.send("/app/game/start", command);
            }

            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                System.err.println(">>> Błąd logiczny WS: " + exception);
                exception.printStackTrace();
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                System.err.println(">>> Błąd transportu (połączenia): " + exception);
                exception.printStackTrace();
            }
        };

        System.out.println(">>> Próba połączenia z: " + url);
        stompClient.connectAsync(url, sessionHandler).get(5, TimeUnit.SECONDS);

        try {
            GameInitializedEvent response = completableFuture.get(10, TimeUnit.SECONDS);

            assertNotNull(response);
            assertEquals("STARTED", response.status());
            System.out.println(">>> TEST ZAKOŃCZONY SUKCESEM. GameID: " + response.gameUuid());

        } catch (TimeoutException e) {
            System.err.println(">>> Timeout! Nie otrzymano odpowiedzi w 10 sekund.");
            throw e;
        }
    }
}
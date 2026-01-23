package com.aidart.backend.player;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    void shouldCreatePlayer() throws Exception {
        PlayerRequest request = new PlayerRequest("Szymon");

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Szymon"));
    }

    @Test
    void shouldNotCreatePlayerWithEmptyName() throws Exception {
        PlayerRequest request = new PlayerRequest("");

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotCreatePlayerWithDuplicateName() throws Exception {
        playerRepository.save(Player.builder()
                .name("Szymon")
                .build());
        PlayerRequest request = new PlayerRequest("Szymon");

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldGetSinglePlayer() throws Exception {
        Player saved = playerRepository.save(Player.builder().name("Darek").build());

        mockMvc.perform(get("/api/players/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Darek"));
    }

    @Test
    void shouldReturn404WhenPlayerDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/players/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllPlayersWithPagination() throws Exception {
        playerRepository.save(Player.builder().name("Player 1").build());
        playerRepository.save(Player.builder().name("Player 2").build());

        mockMvc.perform(get("/api/players?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

}
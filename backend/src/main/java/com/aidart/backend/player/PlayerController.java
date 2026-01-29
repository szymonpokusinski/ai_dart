package com.aidart.backend.player;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public PlayerResponse create(@Valid @RequestBody PlayerRequest request){
        PlayerDTO dtoIn = new PlayerDTO(null, request.name());
        PlayerDTO dtoOut = playerService.create(dtoIn);
        return new PlayerResponse(dtoOut);
    }

    @GetMapping()
    public Page<PlayerDTO> findAll(Pageable pageable){
        return playerService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public PlayerResponse findById(@PathVariable Long id){
        PlayerDTO dto = playerService.findById(id);
        return new PlayerResponse(dto);
    }

    @PutMapping("/{id}")
    public PlayerResponse update(@PathVariable Long id, @Valid @RequestBody PlayerRequest request){
        PlayerDTO dtoIn = new PlayerDTO(id, request.name());
        PlayerDTO dtoOut = playerService.update(id, dtoIn);
        return new PlayerResponse(dtoOut);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        playerService.delete(id);
    }
}

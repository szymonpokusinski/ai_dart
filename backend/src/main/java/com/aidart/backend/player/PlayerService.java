package com.aidart.backend.player;

import com.aidart.backend.exception.DuplicateResourceException;
import com.aidart.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Transactional
    public PlayerDTO create(PlayerDTO playerDTO) {
        if (playerRepository.existsByName(playerDTO.name())) {
            throw new DuplicateResourceException("Player exist");
        }
        try {
            Player player = Player.builder()
                    .name(playerDTO.name())
                    .build();
            Player saved = playerRepository.save(player);
            return new PlayerDTO(saved);

        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();

            if (message != null && message.contains("PLAYER_NAME_UNIQUE_INDEX")) {
                throw new DuplicateResourceException("Player with name [" + playerDTO.name() + "] already exists.");
            }
            throw new RuntimeException("Database integrity error: " + message);
        }
    }

    @Transactional(readOnly = true)
    public Page<PlayerDTO> findAll(Pageable pageable) {
        return playerRepository.findAll(pageable)
                .map(PlayerDTO::new);
    }

    @Transactional(readOnly = true)
    public PlayerDTO findById(Long id){
        return playerRepository.findById(id).map(PlayerDTO::new)
                .orElseThrow(() -> new ResourceNotFoundException("Player with ID [\" + id + \"] not found"));
    }

    public PlayerDTO update(Long id, PlayerDTO playerDTO){
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player with ID [\" + id + \"] not found"));

        if(!player.getName().equals(playerDTO.name()) && playerRepository.existsByName(playerDTO.name())){
            throw new DuplicateResourceException("Name " + playerDTO.name() + " is already taken");
        }

        player.setName(playerDTO.name());

        return new PlayerDTO(player);
    }

    @Transactional
    public void delete(Long id){
        if(playerRepository.existsById(id)){
            return;
        }
        playerRepository.deleteById(id);
    }
}

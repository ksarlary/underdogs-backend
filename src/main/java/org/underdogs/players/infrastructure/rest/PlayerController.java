package org.underdogs.players.infrastructure.rest;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.underdogs.players.application.models.CreatePlayerRequest;
import org.underdogs.players.application.models.UpdatePlayerRequest;
import org.underdogs.players.application.usecases.*;
import org.underdogs.players.domain.PlayerId;
import org.underdogs.players.infrastructure.rest.dto.PlayerDetailDTO;
import org.underdogs.players.infrastructure.rest.dto.PlayerSummaryDTO;
import org.underdogs.players.infrastructure.rest.mapper.PlayerMapper;

@RestController
@RequestMapping("/api/v1/players")
public class PlayerController {

  private final CreatePlayer createPlayer;
  private final SearchPlayerById searchPlayerById;
  private final SearchPlayers searchPlayers;
  private final PlayerMapper playerMapper;
  private final UpdatePlayer updatePlayer;
  private final DeletePlayer deletePlayer;

  public PlayerController(
      CreatePlayer createPlayer,
      SearchPlayerById searchPlayerById,
      SearchPlayers searchPlayers,
      PlayerMapper playerMapper,
      UpdatePlayer updatePlayer,
      DeletePlayer deletePlayer) {
    this.createPlayer = createPlayer;
    this.searchPlayerById = searchPlayerById;
    this.searchPlayers = searchPlayers;
    this.playerMapper = playerMapper;
    this.updatePlayer = updatePlayer;
    this.deletePlayer = deletePlayer;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<Void> create(@Valid @RequestBody CreatePlayerRequest request) {
    final PlayerId playerId = createPlayer.handle(request);
    return ResponseEntity.created(URI.create("/api/v1/players/" + playerId.value())).build();
  }

  @GetMapping
  public ResponseEntity<List<PlayerSummaryDTO>> list() {
    return ResponseEntity.ok(playerMapper.toSummaryDTOList(searchPlayers.handle()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<PlayerDetailDTO> getById(@PathVariable UUID id) {
    return searchPlayerById
        .handle(new PlayerId(id.toString()))
        .map(playerMapper::toDetailDTO)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("/{id}")
  public ResponseEntity<Void> update(
      @PathVariable UUID id, @Valid @RequestBody UpdatePlayerRequest request) {
    updatePlayer.handle(new PlayerId(id.toString()), request);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    deletePlayer.handle(new PlayerId(id.toString()));
    return ResponseEntity.noContent().build();
  }
}

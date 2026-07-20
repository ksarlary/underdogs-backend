package org.underdogs.tournaments.infrastructure.rest;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.underdogs.tournaments.application.models.CreateTournamentRequest;
import org.underdogs.tournaments.application.models.UpdateTournamentRequest;
import org.underdogs.tournaments.application.usecases.CreateTournament;
import org.underdogs.tournaments.application.usecases.DeleteTournament;
import org.underdogs.tournaments.application.usecases.SearchTournamentById;
import org.underdogs.tournaments.application.usecases.SearchTournaments;
import org.underdogs.tournaments.application.usecases.UpdateTournament;
import org.underdogs.tournaments.domain.TournamentId;
import org.underdogs.tournaments.infrastructure.rest.dto.TournamentDetailDTO;
import org.underdogs.tournaments.infrastructure.rest.dto.TournamentSummaryDTO;
import org.underdogs.tournaments.infrastructure.rest.mapper.TournamentMapper;

@RestController
@RequestMapping("/api/v1/tournaments")
public class TournamentController {

  private final CreateTournament createTournament;
  private final SearchTournamentById searchTournamentById;
  private final SearchTournaments searchTournaments;
  private final UpdateTournament updateTournament;
  private final DeleteTournament deleteTournament;
  private final TournamentMapper tournamentMapper;

  public TournamentController(
      CreateTournament createTournament,
      SearchTournamentById searchTournamentById,
      SearchTournaments searchTournaments,
      UpdateTournament updateTournament,
      DeleteTournament deleteTournament,
      TournamentMapper tournamentMapper) {
    this.createTournament = createTournament;
    this.searchTournamentById = searchTournamentById;
    this.searchTournaments = searchTournaments;
    this.updateTournament = updateTournament;
    this.deleteTournament = deleteTournament;
    this.tournamentMapper = tournamentMapper;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<Void> create(@Valid @RequestBody CreateTournamentRequest request) {
    TournamentId tournamentId = createTournament.handle(request);
    return ResponseEntity.created(URI.create("/api/v1/tournaments/" + tournamentId.value()))
        .build();
  }

  @GetMapping
  public ResponseEntity<List<TournamentSummaryDTO>> list() {
    return ResponseEntity.ok(tournamentMapper.toSummaryDTOList(searchTournaments.handle()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<TournamentDetailDTO> getById(@PathVariable UUID id) {
    return searchTournamentById
        .handle(new TournamentId(id.toString()))
        .map(tournamentMapper::toDetailDTO)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("/{id}")
  public ResponseEntity<Void> update(
      @PathVariable UUID id, @Valid @RequestBody UpdateTournamentRequest request) {
    updateTournament.handle(new TournamentId(id.toString()), request);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    deleteTournament.handle(new TournamentId(id.toString()));
    return ResponseEntity.noContent().build();
  }
}

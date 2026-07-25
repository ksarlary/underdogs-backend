package org.underdogs.teams.infrastructure.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.underdogs.shared.rest.PageResponse;
import org.underdogs.teams.application.models.CreateTeamRequest;
import org.underdogs.teams.application.models.UpdateTeamRequest;
import org.underdogs.teams.application.usecases.*;
import org.underdogs.teams.domain.Game;
import org.underdogs.teams.domain.Team;
import org.underdogs.teams.domain.TeamId;
import org.underdogs.teams.infrastructure.rest.dto.TeamDetailDTO;
import org.underdogs.teams.infrastructure.rest.dto.TeamSummaryDTO;
import org.underdogs.teams.infrastructure.rest.mapper.TeamMapper;

@Validated
@RestController
@RequestMapping("/api/v1/teams")
public class TeamController {

  private final CreateTeam createTeam;
  private final SearchTeamById searchTeamById;
  private final SearchTeams searchTeams;
  private final GetTeamStats getTeamStats;
  private final TeamMapper teamMapper;
  private final UpdateTeam updateTeam;
  private final DeleteTeam deleteTeam;

  public TeamController(
      CreateTeam createTeam,
      SearchTeamById searchTeamById,
      SearchTeams searchTeams,
      GetTeamStats getTeamStats,
      TeamMapper teamMapper,
      UpdateTeam updateTeam,
      DeleteTeam deleteTeam) {
    this.createTeam = createTeam;
    this.searchTeamById = searchTeamById;
    this.searchTeams = searchTeams;
    this.getTeamStats = getTeamStats;
    this.teamMapper = teamMapper;
    this.updateTeam = updateTeam;
    this.deleteTeam = deleteTeam;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<Void> create(@Valid @RequestBody CreateTeamRequest request) {
    final TeamId teamId = createTeam.handle(request);
    return ResponseEntity.created(URI.create("/api/v1/teams/" + teamId.value())).build();
  }

  @GetMapping
  public ResponseEntity<PageResponse<TeamSummaryDTO>> list(
      @RequestParam(required = false) Game game,
      @RequestParam(defaultValue = "0") @Min(0) int page,
      @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
    Page<Team> teams = searchTeams.handle(game, PageRequest.of(page, size));

    return ResponseEntity.ok(PageResponse.from(teams.map(teamMapper::toSummaryDTO)));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/stats")
  public ResponseEntity<Map<Game, Long>> stats() {
    return ResponseEntity.ok(getTeamStats.handle());
  }

  @GetMapping("/{id}")
  public ResponseEntity<TeamDetailDTO> getById(@PathVariable UUID id) {
    return searchTeamById
        .handle(new TeamId(id.toString()))
        .map(teamMapper::toDetailDTO)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("/{id}")
  public ResponseEntity<Void> update(
      @PathVariable UUID id, @Valid @RequestBody UpdateTeamRequest request) {
    updateTeam.handle(new TeamId(id.toString()), request);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    deleteTeam.handle(new TeamId(id.toString()));
    return ResponseEntity.noContent().build();
  }
}

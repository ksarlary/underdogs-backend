package org.underdogs.teams.infrastructure.rest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.underdogs.teams.application.models.CreateTeamRequest;
import org.underdogs.teams.application.models.UpdateTeamRequest;
import org.underdogs.teams.application.usecases.*;
import org.underdogs.teams.domain.TeamId;
import org.underdogs.teams.infrastructure.rest.dto.TeamDetailDTO;
import org.underdogs.teams.infrastructure.rest.dto.TeamSummaryDTO;
import org.underdogs.teams.infrastructure.rest.mapper.TeamMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teams")
public class TeamController {

    private final CreateTeam createTeam;
    private final SearchTeamById searchTeamById;
    private final SearchTeams searchTeams;
    private final TeamMapper teamMapper;
    private final UpdateTeam updateTeam;
    private final DeleteTeam deleteTeam;

    public TeamController(
            CreateTeam createTeam,
            SearchTeamById searchTeamById,
            SearchTeams searchTeams,
            TeamMapper teamMapper,
            UpdateTeam updateTeam, DeleteTeam deleteTeam) {
        this.createTeam = createTeam;
        this.searchTeamById = searchTeamById;
        this.searchTeams = searchTeams;
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
    public ResponseEntity<List<TeamSummaryDTO>> list() {
        return ResponseEntity.ok(teamMapper.toSummaryDTOList(searchTeams.handle()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDetailDTO> getById(@PathVariable UUID id) {
        return searchTeamById.handle(new TeamId(id.toString()))
                .map(teamMapper::toDetailDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTeamRequest request
    ) {
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

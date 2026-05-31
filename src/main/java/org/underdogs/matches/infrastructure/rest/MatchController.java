package org.underdogs.matches.infrastructure.rest;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.underdogs.bets.application.models.MatchOddsResponse;
import org.underdogs.bets.application.usecases.GetMatchOdds;
import org.underdogs.matches.application.models.CreateMatchRequest;
import org.underdogs.matches.application.models.UpdateMatchRequest;
import org.underdogs.matches.application.usecases.CreateMatch;
import org.underdogs.matches.application.usecases.DeleteMatch;
import org.underdogs.matches.application.usecases.SearchMatchById;
import org.underdogs.matches.application.usecases.SearchMatches;
import org.underdogs.matches.application.usecases.UpdateMatch;
import org.underdogs.matches.domain.MatchId;
import org.underdogs.matches.infrastructure.rest.dto.MatchDetailDTO;
import org.underdogs.matches.infrastructure.rest.dto.MatchSummaryDTO;
import org.underdogs.matches.infrastructure.rest.mapper.MatchMapper;

@RestController
@RequestMapping("/api/v1/matches")
public class MatchController {

  private final CreateMatch createMatch;
  private final SearchMatchById searchMatchById;
  private final SearchMatches searchMatches;
  private final UpdateMatch updateMatch;
  private final DeleteMatch deleteMatch;
  private final MatchMapper matchMapper;
  private final GetMatchOdds getMatchOdds;

  public MatchController(
      CreateMatch createMatch,
      SearchMatchById searchMatchById,
      SearchMatches searchMatches,
      UpdateMatch updateMatch,
      DeleteMatch deleteMatch,
      MatchMapper matchMapper,
      GetMatchOdds getMatchOdds) {
    this.createMatch = createMatch;
    this.searchMatchById = searchMatchById;
    this.searchMatches = searchMatches;
    this.updateMatch = updateMatch;
    this.deleteMatch = deleteMatch;
    this.matchMapper = matchMapper;
    this.getMatchOdds = getMatchOdds;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<Void> create(@Valid @RequestBody CreateMatchRequest request) {
    MatchId matchId = createMatch.handle(request);
    return ResponseEntity.created(URI.create("/api/v1/matches/" + matchId.value())).build();
  }

  @GetMapping
  public ResponseEntity<List<MatchSummaryDTO>> list() {
    return ResponseEntity.ok(matchMapper.toSummaryDTOList(searchMatches.handle()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<MatchDetailDTO> getById(@PathVariable UUID id) {
    return searchMatchById
        .handle(new MatchId(id.toString()))
        .map(matchMapper::toDetailDTO)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("/{id}")
  public ResponseEntity<Void> update(
      @PathVariable UUID id, @Valid @RequestBody UpdateMatchRequest request) {
    updateMatch.handle(new MatchId(id.toString()), request);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    deleteMatch.handle(new MatchId(id.toString()));
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}/odds")
  public ResponseEntity<MatchOddsResponse> getOdds(@PathVariable String id) {
    return ResponseEntity.ok(getMatchOdds.handle(new MatchId(id)));
  }
}

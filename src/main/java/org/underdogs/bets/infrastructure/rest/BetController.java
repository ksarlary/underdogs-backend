package org.underdogs.bets.infrastructure.rest;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.underdogs.bets.application.models.PlaceBetRequest;
import org.underdogs.bets.application.usecases.PlaceBet;
import org.underdogs.bets.application.usecases.SearchBets;
import org.underdogs.bets.application.usecases.SearchCurrentUserBets;
import org.underdogs.bets.domain.BetId;
import org.underdogs.bets.infrastructure.rest.dto.BetDTO;
import org.underdogs.bets.infrastructure.rest.mapper.BetMapper;

@RestController
@RequestMapping("/api/v1/bets")
public class BetController {

  private final PlaceBet placeBet;
  private final SearchCurrentUserBets searchCurrentUserBets;
  private final SearchBets searchBets;
  private final BetMapper betMapper;

  public BetController(
      PlaceBet placeBet,
      SearchCurrentUserBets searchCurrentUserBets,
      SearchBets searchBets,
      BetMapper betMapper) {
    this.placeBet = placeBet;
    this.searchCurrentUserBets = searchCurrentUserBets;
    this.searchBets = searchBets;
    this.betMapper = betMapper;
  }

  @PostMapping
  public ResponseEntity<Void> place(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody PlaceBetRequest request) {
    BetId betId = placeBet.handle(jwt, request);
    return ResponseEntity.created(URI.create("/api/v1/bets/" + betId.value())).build();
  }

  @GetMapping("/me")
  public ResponseEntity<List<BetDTO>> myBets(@AuthenticationPrincipal Jwt jwt) {
    return ResponseEntity.ok(betMapper.toDTOList(searchCurrentUserBets.handle(jwt)));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ResponseEntity<List<BetDTO>> allBets() {
    return ResponseEntity.ok(betMapper.toDTOList(searchBets.handle()));
  }
}

package org.underdogs.bets.infrastructure.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.net.URI;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.underdogs.bets.application.models.PlaceBetRequest;
import org.underdogs.bets.application.usecases.GetBetStats;
import org.underdogs.bets.application.usecases.PlaceBet;
import org.underdogs.bets.application.usecases.SearchBets;
import org.underdogs.bets.application.usecases.SearchCurrentUserBets;
import org.underdogs.bets.domain.Bet;
import org.underdogs.bets.domain.BetId;
import org.underdogs.bets.domain.BetStatus;
import org.underdogs.bets.infrastructure.rest.dto.BetDTO;
import org.underdogs.bets.infrastructure.rest.mapper.BetMapper;
import org.underdogs.shared.rest.PageResponse;

@Validated
@RestController
@RequestMapping("/api/v1/bets")
public class BetController {

  private final PlaceBet placeBet;
  private final SearchCurrentUserBets searchCurrentUserBets;
  private final SearchBets searchBets;
  private final GetBetStats getBetStats;
  private final BetMapper betMapper;

  public BetController(
      PlaceBet placeBet,
      SearchCurrentUserBets searchCurrentUserBets,
      SearchBets searchBets,
      GetBetStats getBetStats,
      BetMapper betMapper) {
    this.placeBet = placeBet;
    this.searchCurrentUserBets = searchCurrentUserBets;
    this.searchBets = searchBets;
    this.getBetStats = getBetStats;
    this.betMapper = betMapper;
  }

  @PostMapping
  public ResponseEntity<Void> place(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody PlaceBetRequest request) {
    BetId betId = placeBet.handle(jwt, request);
    return ResponseEntity.created(URI.create("/api/v1/bets/" + betId.value())).build();
  }

  @GetMapping("/me")
  public ResponseEntity<PageResponse<BetDTO>> myBets(
      @AuthenticationPrincipal Jwt jwt,
      @RequestParam(required = false) BetStatus status,
      @RequestParam(defaultValue = "0") @Min(0) int page,
      @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
    Page<Bet> bets = searchCurrentUserBets.handle(jwt, status, PageRequest.of(page, size));

    return ResponseEntity.ok(PageResponse.from(bets.map(betMapper::toDTO)));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ResponseEntity<PageResponse<BetDTO>> allBets(
      @RequestParam(required = false) BetStatus status,
      @RequestParam(defaultValue = "0") @Min(0) int page,
      @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
    Page<Bet> bets = searchBets.handle(status, PageRequest.of(page, size));

    return ResponseEntity.ok(PageResponse.from(bets.map(betMapper::toDTO)));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/stats")
  public ResponseEntity<Map<BetStatus, Long>> stats() {
    return ResponseEntity.ok(getBetStats.handle());
  }
}

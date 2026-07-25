package org.underdogs.bets.infrastructure.sqs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record BetResolutionRequest(
    @JsonProperty("correlation_id") String correlationId,
    @JsonProperty("event_id") String eventId,
    @JsonProperty("winning_team_id") String winningTeamId,
    @JsonProperty("bets") List<BetDto> bets) {

  public record BetDto(
      @JsonProperty("bet_id") String betId,
      @JsonProperty("user_id") String userId,
      @JsonProperty("match_id") String matchId,
      @JsonProperty("amount") long amount,
      @JsonProperty("coefficient") double coefficient) {}
}

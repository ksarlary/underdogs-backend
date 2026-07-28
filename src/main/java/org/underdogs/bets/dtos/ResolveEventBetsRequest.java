package org.underdogs.bets.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;

public record ResolveEventBetsRequest(
    @JsonProperty("correlation_id") UUID correlationId, String action, Payload payload) {
  public record Payload(
      @JsonProperty("event_id") String eventId,
      @JsonProperty("winning_team_id") String winningTeamId,
      List<Bet> bets) {}

  public record Bet(
      @JsonProperty("bet_id") String betId,
      @JsonProperty("user_id") String userId,
      @JsonProperty("predicted_team_id") String predictedTeamId,
      @JsonProperty("amount_wagered") int amountWagered,
      double odds) {}
}

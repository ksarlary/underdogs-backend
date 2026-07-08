package org.underdogs.bets.infrastructure.sqs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record BetResolutionResponse(
    @JsonProperty("correlation_id") String correlationId,
    @JsonProperty("status") String status,
    @JsonProperty("results") List<BetResult> results,
    @JsonProperty("error_message") String errorMessage) {

  public record BetResult(
      @JsonProperty("bet_id") String betId, @JsonProperty("match_id") String matchId) {}
}

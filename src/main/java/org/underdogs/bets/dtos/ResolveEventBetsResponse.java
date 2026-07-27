package org.underdogs.bets.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;

public record ResolveEventBetsResponse(
    @JsonProperty("correlation_id") UUID correlationId,
    @JsonProperty("event_id") String eventId,
    String status,
    List<BetResult> results,
    @JsonProperty("error_message") String errorMessage
) {
    public record BetResult(
        @JsonProperty("bet_id") String betId,
        @JsonProperty("user_id") String userId,
        String status,
        @JsonProperty("kibbles_to_credit") int kibblesToCredit
    ) {}
}
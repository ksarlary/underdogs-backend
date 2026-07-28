package org.underdogs.bets;

import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.underdogs.bets.dtos.ResolveEventBetsRequest;
import org.underdogs.bets.dtos.ResolveEventBetsResponse;

@Component
public class TestWorker {

  @Autowired private SqsTemplate sqsTemplate;

  @SqsListener("underdogs-requests-queue")
  public void receiveBetResolutionRequest(ResolveEventBetsRequest request) {
    if (!"RESOLVE_EVENT_BETS".equals(request.action())) {
      // Create and send a FAILURE response
      return;
    }

    List<ResolveEventBetsResponse.BetResult> results =
        request.payload().bets().stream()
            .map(
                bet -> {
                  if (bet.predictedTeamId().equals(request.payload().winningTeamId())) {
                    return new ResolveEventBetsResponse.BetResult(
                        bet.betId(), bet.userId(), "WON", (int) (bet.amountWagered() * bet.odds()));
                  } else {
                    return new ResolveEventBetsResponse.BetResult(
                        bet.betId(), bet.userId(), "LOST", 0);
                  }
                })
            .collect(Collectors.toList());

    ResolveEventBetsResponse response =
        new ResolveEventBetsResponse(
            request.correlationId(), request.payload().eventId(), "SUCCESS", results, null);

    sqsTemplate.send(to -> to.queue("underdogs-responses-queue").payload(response));
  }
}

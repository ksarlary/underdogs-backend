package org.underdogs.bets.infrastructure.rest;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.underdogs.bets.application.services.BetResolutionPublisher;
import org.underdogs.bets.infrastructure.sqs.dto.BetResolutionRequest.BetDto;

@RestController
@RequestMapping("/api/test/bets")
public class BetResolutionTestController {

  private final BetResolutionPublisher betResolutionPublisher;

  public BetResolutionTestController(BetResolutionPublisher betResolutionPublisher) {
    this.betResolutionPublisher = betResolutionPublisher;
  }

  @PostMapping("/resolve")
  public ResponseEntity<String> resolve(@RequestBody TestResolutionRequest request) {
    betResolutionPublisher.publishResolutionRequest(
        request.eventId(), request.winningTeamId(), request.bets());
    return ResponseEntity.accepted().body("Requête envoyée à SQS avec succès !");
  }

  public record TestResolutionRequest(
      String eventId, String winningTeamId, List<BetDto> bets) {}
}

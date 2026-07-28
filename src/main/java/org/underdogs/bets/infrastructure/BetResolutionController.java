package org.underdogs.bets.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.underdogs.bets.dtos.ResolveEventBetsRequest;

@RestController
@RequestMapping("/bets/resolution")
public class BetResolutionController {

  private final BetResolutionPublisherService betResolutionPublisherService;

  @Autowired
  public BetResolutionController(BetResolutionPublisherService betResolutionPublisherService) {
    this.betResolutionPublisherService = betResolutionPublisherService;
  }

  @PostMapping
  public void resolveBets(@RequestBody ResolveEventBetsRequest request) {
    betResolutionPublisherService.publishBetResolutionRequest(request);
  }
}

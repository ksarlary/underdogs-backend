package org.underdogs.bets.application.services;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.underdogs.bets.infrastructure.sqs.dto.BetResolutionRequest;
import org.underdogs.bets.infrastructure.sqs.dto.BetResolutionRequest.BetDto;
import org.underdogs.bets.infrastructure.sqs.SqsMessagePublisher;

@Service
public class BetResolutionPublisher {

  private static final Logger logger = LoggerFactory.getLogger(BetResolutionPublisher.class);

  private final SqsMessagePublisher sqsMessagePublisher;

  public BetResolutionPublisher(SqsMessagePublisher sqsMessagePublisher) {
    this.sqsMessagePublisher = sqsMessagePublisher;
  }

  public void publishResolutionRequest(
      String eventId, String winningTeamId, List<BetDto> bets) {
    String correlationId = UUID.randomUUID().toString();

    BetResolutionRequest request =
        new BetResolutionRequest(correlationId, eventId, winningTeamId, bets);

    sqsMessagePublisher.sendMessage("underdogs-requests-queue", request);

    logger.info(
        "Demande de résolution envoyée à SQS avec correlation_id: {}", correlationId);
  }
}


package org.underdogs.bets.infrastructure.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.underdogs.bets.application.BetResolutionService;
import org.underdogs.bets.dtos.ResolveEventBetsResponse;

@Component
public class BetResolutionSqsListener {

  private static final Logger logger = LoggerFactory.getLogger(BetResolutionSqsListener.class);

  private final BetResolutionService betResolutionService;
  private final ObjectMapper objectMapper;

  public BetResolutionSqsListener(
      BetResolutionService betResolutionService, ObjectMapper objectMapper) {
    this.betResolutionService = betResolutionService;
    this.objectMapper = objectMapper;
  }

  public void handleBetResolutionMessage(String message) {
    try {
      logger.debug("SQS message received for bet resolution: {}", message);

      ResolveEventBetsResponse response =
          objectMapper.readValue(message, ResolveEventBetsResponse.class);

      logger.info(
          "SQS message deserialized with correlation_id: {}, status: {}",
          response.correlationId(),
          response.status());

      betResolutionService.processBetResolution(response);

      logger.info(
          "SQS message processed successfully for correlation_id: {}", response.correlationId());
    } catch (Exception e) {
      logger.error("Error processing SQS message: {}", message, e);
      throw new SqsListenerException("Unable to process bet resolution SQS message", e);
    }
  }

  public static class SqsListenerException extends RuntimeException {
    public SqsListenerException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}

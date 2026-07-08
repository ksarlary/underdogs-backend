package org.underdogs.bets.infrastructure.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.underdogs.bets.application.services.BetResolutionService;
import org.underdogs.bets.infrastructure.sqs.dto.BetResolutionResponse;

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
      logger.debug("Message SQS reçu pour résolution des paris: {}", message);

      BetResolutionResponse response =
          objectMapper.readValue(message, BetResolutionResponse.class);

      logger.info(
          "Message SQS désérialisé avec correlation_id: {}, status: {}",
          response.correlationId(),
          response.status());

      betResolutionService.processBetResolution(response);

      logger.info(
          "Message SQS traité avec succès pour correlation_id: {}", response.correlationId());
    } catch (Exception e) {
      logger.error("Erreur lors du traitement du message SQS: {}", message, e);
      throw new SqsListenerException(
          "Impossible de traiter le message SQS de résolution des paris", e);
    }
  }

  public static class SqsListenerException extends RuntimeException {
    public SqsListenerException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}

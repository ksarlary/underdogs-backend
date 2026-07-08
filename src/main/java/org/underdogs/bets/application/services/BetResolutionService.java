package org.underdogs.bets.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.underdogs.bets.infrastructure.sqs.dto.BetResolutionResponse;

@Service
public class BetResolutionService {

  private static final Logger logger = LoggerFactory.getLogger(BetResolutionService.class);

  public void processBetResolution(BetResolutionResponse response) {
    logger.info(
        "Traitement de la résolution des paris pour correlation_id: {}",
        response.correlationId());

    if ("success".equalsIgnoreCase(response.status())) {
      handleSuccessfulResolution(response);
    } else if ("failure".equalsIgnoreCase(response.status())) {
      handleFailedResolution(response);
    } else {
      logger.warn(
          "Statut de résolution inconnu: {} pour correlation_id: {}",
          response.status(),
          response.correlationId());
    }
  }

  private void handleSuccessfulResolution(BetResolutionResponse response) {
    logger.info(
        "Résolution réussie pour {} paris avec correlation_id: {}",
        response.results().size(),
        response.correlationId());
  }

  private void handleFailedResolution(BetResolutionResponse response) {
    logger.error(
        "Erreur lors de la résolution des paris pour correlation_id: {}. Message d'erreur: {}",
        response.correlationId(),
        response.errorMessage());
  }
}

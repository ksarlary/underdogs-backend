package org.underdogs.bets.infrastructure;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.underdogs.bets.application.BetResolutionService;
import org.underdogs.bets.dtos.ResolveEventBetsResponse;

@Component
public class BetResolutionListener {

    private static final Logger logger = LoggerFactory.getLogger(BetResolutionListener.class);

    private final BetResolutionService betResolutionService;

    @Autowired
    public BetResolutionListener(BetResolutionService betResolutionService) {
        this.betResolutionService = betResolutionService;
    }

    @SqsListener("${sqs.queues.response-queue-name}")
    public void receiveBetResolutionResponse(ResolveEventBetsResponse response) {
        logger.info("Received bet resolution response with correlation_id: {}", response.correlationId());
        try {
            betResolutionService.processBetResolution(response);
        } catch (Exception e) {
            logger.error("Failed to process bet resolution for event_id: {}. Error: {}", response.eventId(), e.getMessage(), e);
            // Here you would implement your requeueing or DLQ logic
            // For now, we just log the error
        }
    }
}
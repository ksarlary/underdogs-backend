package org.underdogs.bets.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.underdogs.bets.domain.Bet;
import org.underdogs.bets.domain.BetRepository;
import org.underdogs.bets.dtos.ResolveEventBetsResponse;

import java.util.UUID;

@Service
public class BetResolutionService {

    private static final Logger logger = LoggerFactory.getLogger(BetResolutionService.class);

    private final BetRepository betRepository;

    @Autowired
    public BetResolutionService(BetRepository betRepository) {
        this.betRepository = betRepository;
    }

    @Transactional
    public void processBetResolution(ResolveEventBetsResponse response) {
        logger.info("Processing bet resolution for event_id: {}", response.eventId());

        for (ResolveEventBetsResponse.BetResult result : response.results()) {
            Bet bet = betRepository.findById(UUID.fromString(result.betId())).orElse(null);
            if (bet != null) {
                bet.setStatus(result.status());
                betRepository.save(bet);
            }
        }

        logger.info("Successfully processed bet resolution for event_id: {}", response.eventId());
    }
}
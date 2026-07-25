package org.underdogs.bets.infrastructure.rest.mapper;

import org.springframework.stereotype.Component;
import org.underdogs.bets.domain.Bet;
import org.underdogs.bets.infrastructure.rest.dto.BetDTO;

@Component
public class BetMapper {

  public BetDTO toDTO(Bet bet) {
    return new BetDTO(
        bet.getId().value(),
        bet.getMatch().getId().value(),
        bet.getMatch().getTeam1().getName(),
        bet.getMatch().getTeam2().getName(),
        bet.getSelectedTeam().getId().value(),
        bet.getSelectedTeam().getName(),
        bet.getUser().getUsername(),
        bet.getAmount(),
        bet.getCoefficient(),
        bet.getPotentialGain(),
        bet.getStatus(),
        bet.getCreatedAt(),
        bet.getResolvedAt());
  }
}

package org.underdogs.bets.infrastructure.rest.mapper;

import java.util.List;
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
        bet.getAmount(),
        bet.getCoefficient(),
        bet.getPotentialGain(),
        bet.getStatus(),
        bet.getCreatedAt(),
        bet.getResolvedAt());
  }

  public List<BetDTO> toDTOList(List<Bet> bets) {
    return bets.stream().map(this::toDTO).toList();
  }
}

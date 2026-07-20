package org.underdogs.players.infrastructure.rest.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import org.underdogs.players.domain.Player;
import org.underdogs.players.infrastructure.rest.dto.PlayerDetailDTO;
import org.underdogs.players.infrastructure.rest.dto.PlayerSummaryDTO;

@Component
public class PlayerMapper {

  public PlayerSummaryDTO toSummaryDTO(Player player) {
    return new PlayerSummaryDTO(
        player.getId().value(),
        player.getNickname(),
        player.getFullName(),
        player.getRole(),
        player.getCountryCode(),
        player.getTeam().getId().value(),
        player.getTeam().getName());
  }

  public PlayerDetailDTO toDetailDTO(Player player) {
    return new PlayerDetailDTO(
        player.getId().value(),
        player.getNickname(),
        player.getFullName(),
        player.getRole(),
        player.getCountryCode(),
        player.getTeam().getId().value(),
        player.getTeam().getName(),
        player.getTeam().getGame().name());
  }

  public List<PlayerSummaryDTO> toSummaryDTOList(List<Player> players) {
    return players.stream().map(this::toSummaryDTO).toList();
  }
}

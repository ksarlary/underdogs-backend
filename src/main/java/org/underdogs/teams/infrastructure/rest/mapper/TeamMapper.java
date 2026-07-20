package org.underdogs.teams.infrastructure.rest.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import org.underdogs.players.infrastructure.rest.dto.PlayerInTeamDTO;
import org.underdogs.teams.domain.Team;
import org.underdogs.teams.infrastructure.rest.dto.TeamDetailDTO;
import org.underdogs.teams.infrastructure.rest.dto.TeamSummaryDTO;

@Component
public class TeamMapper {

  public TeamSummaryDTO toSummaryDTO(Team team) {
    return new TeamSummaryDTO(
        team.getId().value(), team.getName(), team.getTag(), team.getGame().name());
  }

  public TeamDetailDTO toDetailDTO(Team team) {
    return new TeamDetailDTO(
        team.getId().value(),
        team.getName(),
        team.getTag(),
        team.getGame().name(),
        mapPlayers(team));
  }

  private List<PlayerInTeamDTO> mapPlayers(Team team) {
    return team.getPlayers().stream()
        .map(
            player ->
                new PlayerInTeamDTO(
                    player.getId().value(), player.getNickname(), player.getCountryCode()))
        .toList();
  }

  public List<TeamSummaryDTO> toSummaryDTOList(List<Team> teams) {
    return teams.stream().map(this::toSummaryDTO).toList();
  }
}

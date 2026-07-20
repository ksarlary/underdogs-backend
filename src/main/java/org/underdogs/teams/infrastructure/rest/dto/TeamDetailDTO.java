package org.underdogs.teams.infrastructure.rest.dto;

import java.util.List;
import org.underdogs.players.infrastructure.rest.dto.PlayerInTeamDTO;

public record TeamDetailDTO(
    String id, String name, String tag, String game, List<PlayerInTeamDTO> players) {}

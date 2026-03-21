package org.underdogs.teams.infrastructure.rest.dto;


import org.underdogs.players.infrastructure.rest.dto.PlayerInTeamDTO;

import java.util.List;

public record TeamDetailDTO(
        String id,
        String name,
        String tag,
        String game,
        List<PlayerInTeamDTO> players
) {
}

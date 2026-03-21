package org.underdogs.teams.application.services;

import org.underdogs.shared.error.BusinessException;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.teams.application.gateways.TeamRepository;
import org.underdogs.teams.application.usecases.DeleteTeam;
import org.underdogs.teams.domain.TeamId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class DeleteTeamHandler implements DeleteTeam {

    private final TeamRepository teamRepository;

    DeleteTeamHandler(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    @Transactional
    public void handle(TeamId id) {
        final var team = teamRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        BusinessErrorCodes.TEAM_NOT_FOUND,
                        "Team not found"
                ));

        teamRepository.delete(team);
    }
}

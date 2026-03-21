package org.underdogs.teams.application.services;

import org.underdogs.shared.error.BusinessException;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.teams.application.gateways.TeamRepository;
import org.underdogs.teams.application.models.UpdateTeamRequest;
import org.underdogs.teams.application.usecases.UpdateTeam;
import org.underdogs.teams.domain.TeamId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class UpdateTeamHandler implements UpdateTeam {

    private final TeamRepository teamRepository;

    UpdateTeamHandler(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    @Transactional
    public void handle(TeamId id, UpdateTeamRequest request) {
        final var team = teamRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        BusinessErrorCodes.TEAM_NOT_FOUND,
                        "Team not found"
                ));

        if (request.name() != null && !request.name().equals(team.getName())) {
            teamRepository.findByName(request.name())
                    .filter(existing -> !existing.getId().equals(team.getId()))
                    .ifPresent(existing -> {
                        throw new BusinessException(
                                BusinessErrorCodes.TEAM_NAME_ALREADY_EXISTS,
                                "A team with this name already exists"
                        );
                    });
        }

        if (request.tag() != null && !request.tag().equals(team.getTag())) {
            teamRepository.findByTag(request.tag())
                    .filter(existing -> !existing.getId().equals(team.getId()))
                    .ifPresent(existing -> {
                        throw new BusinessException(
                                BusinessErrorCodes.TEAM_TAG_ALREADY_EXISTS,
                                "A team with this tag already exists"
                        );
                    });
        }

        team.update(request.name(), request.tag(), request.game());
        teamRepository.save(team);
    }
}

package org.underdogs.teams.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.underdogs.shared.DomainIdGenerator;
import org.underdogs.shared.error.BusinessException;
import org.underdogs.teams.application.gateways.TeamRepository;
import org.underdogs.teams.application.models.CreateTeamRequest;
import org.underdogs.teams.application.usecases.CreateTeam;
import org.underdogs.teams.domain.Team;
import org.underdogs.teams.domain.TeamId;

@Service
class CreateTeamHandler implements CreateTeam {

  private final TeamRepository teamRepository;
  private final DomainIdGenerator domainIdGenerator;

  CreateTeamHandler(TeamRepository teamRepository, DomainIdGenerator domainIdGenerator) {
    this.teamRepository = teamRepository;
    this.domainIdGenerator = domainIdGenerator;
  }

  @Override
  @Transactional
  public TeamId handle(CreateTeamRequest request) {
    teamRepository
        .findByName(request.name())
        .ifPresent(
            team -> {
              throw new BusinessException(
                  "TEAM_NAME_ALREADY_EXISTS", "A team with this name already exists");
            });

    teamRepository
        .findByTag(request.tag())
        .ifPresent(
            team -> {
              throw new BusinessException(
                  "TEAM_TAG_ALREADY_EXISTS", "A team with this tag already exists");
            });

    final TeamId teamId = new TeamId(domainIdGenerator.generate());

    final Team team = Team.create(teamId, request.name(), request.tag(), request.game());

    teamRepository.save(team);

    return teamId;
  }
}

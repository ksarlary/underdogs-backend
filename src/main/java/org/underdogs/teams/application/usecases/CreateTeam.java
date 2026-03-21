package org.underdogs.teams.application.usecases;

import org.underdogs.teams.application.models.CreateTeamRequest;
import org.underdogs.teams.domain.TeamId;

public interface CreateTeam {
    TeamId handle(CreateTeamRequest request);
}
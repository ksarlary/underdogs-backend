package org.underdogs.teams.application.usecases;

import org.underdogs.teams.application.models.UpdateTeamRequest;
import org.underdogs.teams.domain.TeamId;

public interface UpdateTeam {
    void handle(TeamId id, UpdateTeamRequest request);
}

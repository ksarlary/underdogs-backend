package org.underdogs.teams.application.usecases;

import org.underdogs.teams.domain.TeamId;

public interface DeleteTeam {
    void handle(TeamId id);
}

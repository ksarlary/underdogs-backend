package org.underdogs.teams.application.usecases;

import org.underdogs.teams.domain.Team;

import java.util.List;

public interface SearchTeams {
    List<Team> handle();
}
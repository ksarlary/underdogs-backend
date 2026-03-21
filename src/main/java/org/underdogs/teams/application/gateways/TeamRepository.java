package org.underdogs.teams.application.gateways;

import org.underdogs.teams.domain.Team;
import org.underdogs.teams.domain.TeamId;

import java.util.List;
import java.util.Optional;

public interface TeamRepository {
    void save(Team team);
    void delete(Team team);
    Optional<Team> findById(TeamId id);
    Optional<Team> findByName(String name);
    Optional<Team> findByTag(String tag);
    List<Team> findAll();
}
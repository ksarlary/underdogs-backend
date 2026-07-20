package org.underdogs.tournaments.application.gateways;

import java.util.List;
import java.util.Optional;
import org.underdogs.tournaments.domain.Tournament;
import org.underdogs.tournaments.domain.TournamentId;

public interface TournamentRepository {
  void save(Tournament tournament);

  void delete(Tournament tournament);

  Optional<Tournament> findById(TournamentId id);

  Optional<Tournament> findByName(String name);

  List<Tournament> findAll();
}

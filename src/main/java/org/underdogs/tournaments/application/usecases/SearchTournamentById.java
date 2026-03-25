package org.underdogs.tournaments.application.usecases;

import java.util.Optional;
import org.underdogs.tournaments.domain.Tournament;
import org.underdogs.tournaments.domain.TournamentId;

public interface SearchTournamentById {
  Optional<Tournament> handle(TournamentId id);
}

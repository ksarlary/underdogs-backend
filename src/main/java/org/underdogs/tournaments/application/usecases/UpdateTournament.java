package org.underdogs.tournaments.application.usecases;

import org.underdogs.tournaments.application.models.UpdateTournamentRequest;
import org.underdogs.tournaments.domain.TournamentId;

public interface UpdateTournament {
  void handle(TournamentId id, UpdateTournamentRequest request);
}

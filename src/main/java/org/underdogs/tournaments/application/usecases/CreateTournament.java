package org.underdogs.tournaments.application.usecases;

import org.underdogs.tournaments.application.models.CreateTournamentRequest;
import org.underdogs.tournaments.domain.TournamentId;

public interface CreateTournament {
  TournamentId handle(CreateTournamentRequest request);
}

package org.underdogs.tournaments.application.usecases;

import org.underdogs.tournaments.domain.TournamentId;

public interface DeleteTournament {
  void handle(TournamentId id);
}

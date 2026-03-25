package org.underdogs.tournaments.application.usecases;

import java.util.List;
import org.underdogs.tournaments.domain.Tournament;

public interface SearchTournaments {
  List<Tournament> handle();
}

package org.underdogs.matches.application.usecases;

import org.underdogs.matches.domain.MatchId;

public interface DeleteMatch {
  void handle(MatchId id);
}

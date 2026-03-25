package org.underdogs.matches.application.usecases;

import org.underdogs.matches.application.models.UpdateMatchRequest;
import org.underdogs.matches.domain.MatchId;

public interface UpdateMatch {
  void handle(MatchId id, UpdateMatchRequest request);
}

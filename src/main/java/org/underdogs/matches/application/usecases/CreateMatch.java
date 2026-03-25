package org.underdogs.matches.application.usecases;

import org.underdogs.matches.application.models.CreateMatchRequest;
import org.underdogs.matches.domain.MatchId;

public interface CreateMatch {
  MatchId handle(CreateMatchRequest request);
}

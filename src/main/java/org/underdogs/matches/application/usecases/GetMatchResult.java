package org.underdogs.matches.application.usecases;

import org.underdogs.matches.domain.Match;

public interface GetMatchResult {

  Match handle(String matchId);
}

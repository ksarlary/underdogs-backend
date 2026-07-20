package org.underdogs.matches.application.usecases;

import java.util.Optional;
import org.underdogs.matches.domain.Match;
import org.underdogs.matches.domain.MatchId;

public interface SearchMatchById {
  Optional<Match> handle(MatchId id);
}

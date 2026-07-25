package org.underdogs.matches.application.gateways;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.underdogs.matches.domain.Match;
import org.underdogs.matches.domain.MatchId;

public interface MatchRepository {
  void save(Match match);

  void delete(Match match);

  Optional<Match> findById(MatchId id);

  List<Match> findAll();

  List<Match> findScheduledMatchesToStart(LocalDateTime now);
}

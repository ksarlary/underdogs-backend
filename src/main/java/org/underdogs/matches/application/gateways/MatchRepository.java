package org.underdogs.matches.application.gateways;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.underdogs.matches.domain.Match;
import org.underdogs.matches.domain.MatchId;
import org.underdogs.matches.domain.MatchStatus;
import org.underdogs.teams.domain.Game;

public interface MatchRepository {
  void save(Match match);

  void delete(Match match);

  Optional<Match> findById(MatchId id);

  List<Match> findScheduledMatchesToStart(Instant now);

  Page<Match> search(Game game, MatchStatus status, Pageable pageable);

  Map<MatchStatus, Long> countByStatus();
}

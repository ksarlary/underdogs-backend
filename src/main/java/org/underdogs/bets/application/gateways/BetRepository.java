package org.underdogs.bets.application.gateways;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.underdogs.bets.domain.Bet;
import org.underdogs.bets.domain.BetId;
import org.underdogs.bets.domain.BetStatus;
import org.underdogs.matches.domain.Match;
import org.underdogs.teams.domain.Team;
import org.underdogs.users.domain.User;

public interface BetRepository {
  void save(Bet bet);

  Optional<Bet> findById(BetId id);

  List<Bet> findByMatch(Match match);

  boolean existsByUserAndMatch(User user, Match match);

  long sumAmountByMatchAndSelectedTeam(Match match, Team selectedTeam);

  long sumAmountByMatch(Match match);

  Page<Bet> findByUser(User user, BetStatus status, Pageable pageable);

  Page<Bet> search(BetStatus status, Pageable pageable);

  Map<BetStatus, Long> countByStatus();
}

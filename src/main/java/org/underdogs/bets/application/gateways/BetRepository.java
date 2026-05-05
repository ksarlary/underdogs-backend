package org.underdogs.bets.application.gateways;

import java.util.List;
import java.util.Optional;
import org.underdogs.bets.domain.Bet;
import org.underdogs.bets.domain.BetId;
import org.underdogs.matches.domain.Match;
import org.underdogs.teams.domain.Team;
import org.underdogs.users.domain.User;

public interface BetRepository {
  void save(Bet bet);

  Optional<Bet> findById(BetId id);

  List<Bet> findAll();

  List<Bet> findByUser(User user);

  boolean existsByUserAndMatch(User user, Match match);

  long sumAmountByMatchAndSelectedTeam(Match match, Team selectedTeam);

  long sumAmountByMatch(Match match);

  List<Bet> findByMatch(Match match);
}

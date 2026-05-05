package org.underdogs.bets.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.underdogs.bets.domain.Bet;
import org.underdogs.bets.domain.BetId;
import org.underdogs.matches.domain.Match;
import org.underdogs.teams.domain.Team;
import org.underdogs.users.domain.User;

interface SpringJpaBetRepository extends JpaRepository<Bet, Long> {
  Optional<Bet> findById(BetId id);

  List<Bet> findByUser(User user);

  boolean existsByUserAndMatch(User user, Match match);

  @Query(
      """
    SELECT COALESCE(SUM(b.amount), 0)
    FROM Bet b
    WHERE b.match = :match
    AND b.selectedTeam = :selectedTeam
    """)
  long sumAmountByMatchAndSelectedTeam(
      @Param("match") Match match, @Param("selectedTeam") Team selectedTeam);

  @Query(
      """
    SELECT COALESCE(SUM(b.amount), 0)
    FROM Bet b
    WHERE b.match = :match
    """)
  long sumAmountByMatch(@Param("match") Match match);

  List<Bet> findByMatch(Match match);
}

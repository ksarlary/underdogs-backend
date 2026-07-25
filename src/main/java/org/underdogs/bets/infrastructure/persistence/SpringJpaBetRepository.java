package org.underdogs.bets.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.underdogs.bets.domain.Bet;
import org.underdogs.bets.domain.BetId;
import org.underdogs.bets.domain.BetStatus;
import org.underdogs.matches.domain.Match;
import org.underdogs.teams.domain.Team;
import org.underdogs.users.domain.User;

interface SpringJpaBetRepository extends JpaRepository<Bet, Long> {
  Optional<Bet> findById(BetId id);

  List<Bet> findByMatch(Match match);

  boolean existsByUserAndMatch(User user, Match match);

  @Query(
      """
    SELECT b FROM Bet b
    WHERE b.user = :user AND (:status IS NULL OR b.status = :status)
    ORDER BY b.createdAt DESC
    """)
  Page<Bet> findByUser(
      @Param("user") User user, @Param("status") BetStatus status, Pageable pageable);

  @Query(
      """
    SELECT b FROM Bet b
    WHERE (:status IS NULL OR b.status = :status)
    ORDER BY b.createdAt DESC
    """)
  Page<Bet> search(@Param("status") BetStatus status, Pageable pageable);

  long countByStatus(BetStatus status);

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
}

package org.underdogs.bets.infrastructure.persistence;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.underdogs.bets.application.gateways.BetRepository;
import org.underdogs.bets.domain.Bet;
import org.underdogs.bets.domain.BetId;
import org.underdogs.bets.domain.BetStatus;
import org.underdogs.matches.domain.Match;
import org.underdogs.teams.domain.Team;
import org.underdogs.users.domain.User;

@Repository
class JpaBetRepository implements BetRepository {

  private final SpringJpaBetRepository jpaRepository;

  JpaBetRepository(SpringJpaBetRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public void save(Bet bet) {
    jpaRepository.save(bet);
  }

  @Override
  public Optional<Bet> findById(BetId id) {
    return jpaRepository.findById(id);
  }

  @Override
  public boolean existsByUserAndMatch(User user, Match match) {
    return jpaRepository.existsByUserAndMatch(user, match);
  }

  @Override
  public long sumAmountByMatchAndSelectedTeam(Match match, Team selectedTeam) {
    return jpaRepository.sumAmountByMatchAndSelectedTeam(match, selectedTeam);
  }

  @Override
  public long sumAmountByMatch(Match match) {
    return jpaRepository.sumAmountByMatch(match);
  }

  @Override
  public List<Bet> findByMatch(Match match) {
    return jpaRepository.findByMatch(match);
  }

  @Override
  public Page<Bet> findByUser(User user, BetStatus status, Pageable pageable) {
    return jpaRepository.findByUser(user, status, pageable);
  }

  @Override
  public Page<Bet> search(BetStatus status, Pageable pageable) {
    return jpaRepository.search(status, pageable);
  }

  @Override
  public Map<BetStatus, Long> countByStatus() {
    return Arrays.stream(BetStatus.values())
        .collect(Collectors.toMap(status -> status, jpaRepository::countByStatus));
  }
}

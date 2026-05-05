package org.underdogs.bets.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.underdogs.bets.application.gateways.BetRepository;
import org.underdogs.bets.domain.Bet;
import org.underdogs.bets.domain.BetId;
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
  public List<Bet> findAll() {
    return jpaRepository.findAll();
  }

  @Override
  public List<Bet> findByUser(User user) {
    return jpaRepository.findByUser(user);
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
}

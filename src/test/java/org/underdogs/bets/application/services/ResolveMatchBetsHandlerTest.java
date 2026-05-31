package org.underdogs.bets.application.services;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.underdogs.bets.application.gateways.BetRepository;
import org.underdogs.bets.domain.Bet;
import org.underdogs.bets.domain.BetId;
import org.underdogs.matches.domain.Match;
import org.underdogs.matches.domain.MatchId;
import org.underdogs.matches.domain.MatchStatus;
import org.underdogs.shared.TimeProvider;
import org.underdogs.teams.domain.Game;
import org.underdogs.teams.domain.Team;
import org.underdogs.teams.domain.TeamId;
import org.underdogs.tournaments.domain.Tournament;
import org.underdogs.tournaments.domain.TournamentId;
import org.underdogs.users.application.gateways.UserRepository;
import org.underdogs.users.domain.User;

class ResolveMatchBetsHandlerTest {

  private BetRepository betRepository;
  private UserRepository userRepository;
  private TimeProvider timeProvider;
  private ResolveMatchBetsHandler handler;

  @BeforeEach
  void setUp() {
    betRepository = mock(BetRepository.class);
    userRepository = mock(UserRepository.class);
    timeProvider = mock(TimeProvider.class);

    handler = new ResolveMatchBetsHandler(betRepository, userRepository, timeProvider);
  }

  @Test
  void shouldResolveWinningAndLosingBetsWhenMatchIsFinished() {
    User winningUser = mock(User.class);
    User losingUser = mock(User.class);

    Team team1 = Team.create(new TeamId("team-1"), "T1", "T1", Game.LEAGUE_OF_LEGENDS);
    Team team2 = Team.create(new TeamId("team-2"), "Gen.G", "GEN", Game.LEAGUE_OF_LEGENDS);

    Match match = createMatch(team1, team2);
    match.update(null, null, null, null, null, MatchStatus.FINISHED, 2, 1, team1);

    Bet winningBet =
        Bet.create(
            new BetId("bet-1"),
            winningUser,
            match,
            team1,
            100,
            2.0,
            200,
            Instant.parse("2026-03-21T10:00:00Z"));

    Bet losingBet =
        Bet.create(
            new BetId("bet-2"),
            losingUser,
            match,
            team2,
            100,
            2.0,
            200,
            Instant.parse("2026-03-21T10:00:00Z"));

    when(timeProvider.now()).thenReturn(Instant.parse("2026-03-21T12:00:00Z"));
    when(betRepository.findByMatch(match)).thenReturn(List.of(winningBet, losingBet));

    handler.handle(match);

    verify(winningUser).creditKibbles(200);
    verify(userRepository).save(winningUser);

    verify(betRepository).save(winningBet);
    verify(betRepository).save(losingBet);
  }

  @Test
  void shouldCancelAndRefundBetsWhenMatchIsCancelled() {
    User user = mock(User.class);

    Team team1 = Team.create(new TeamId("team-1"), "T1", "T1", Game.LEAGUE_OF_LEGENDS);
    Team team2 = Team.create(new TeamId("team-2"), "Gen.G", "GEN", Game.LEAGUE_OF_LEGENDS);

    Match match = createMatch(team1, team2);
    match.update(null, null, null, null, null, MatchStatus.CANCELLED, null, null, null);

    Bet bet =
        Bet.create(
            new BetId("bet-1"),
            user,
            match,
            team1,
            100,
            2.0,
            200,
            Instant.parse("2026-03-21T10:00:00Z"));

    when(timeProvider.now()).thenReturn(Instant.parse("2026-03-21T12:00:00Z"));
    when(betRepository.findByMatch(match)).thenReturn(List.of(bet));

    handler.handle(match);

    verify(user).creditKibbles(100);
    verify(userRepository).save(user);
    verify(betRepository).save(bet);
  }

  private Match createMatch(Team team1, Team team2) {
    Tournament tournament =
        Tournament.create(
            new TournamentId("tournament-1"),
            "Worlds 2026",
            Game.LEAGUE_OF_LEGENDS,
            LocalDate.of(2026, 10, 1),
            LocalDate.of(2026, 11, 5));

    return Match.create(
        new MatchId("match-1"),
        team1,
        team2,
        tournament,
        Game.LEAGUE_OF_LEGENDS,
        LocalDateTime.of(2026, 10, 10, 18, 0));
  }
}

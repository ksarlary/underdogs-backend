package org.underdogs.bets.application.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.underdogs.bets.application.gateways.BetRepository;
import org.underdogs.matches.application.gateways.MatchRepository;
import org.underdogs.matches.domain.Match;
import org.underdogs.matches.domain.MatchId;
import org.underdogs.teams.domain.Game;
import org.underdogs.teams.domain.Team;
import org.underdogs.teams.domain.TeamId;
import org.underdogs.tournaments.domain.Tournament;
import org.underdogs.tournaments.domain.TournamentId;

class GetMatchOddsHandlerTest {

  private MatchRepository matchRepository;
  private BetRepository betRepository;
  private BetCoefficientCalculator coefficientCalculator;
  private GetMatchOddsHandler handler;

  @BeforeEach
  void setUp() {
    matchRepository = mock(MatchRepository.class);
    betRepository = mock(BetRepository.class);
    coefficientCalculator = mock(BetCoefficientCalculator.class);

    handler = new GetMatchOddsHandler(matchRepository, betRepository, coefficientCalculator);
  }

  @Test
  void shouldReturnOddsForBothTeams() {
    Team team1 = Team.create(new TeamId("team-1"), "T1", "T1", Game.LEAGUE_OF_LEGENDS);
    Team team2 = Team.create(new TeamId("team-2"), "Gen.G", "GEN", Game.LEAGUE_OF_LEGENDS);

    Tournament tournament =
        Tournament.create(
            new TournamentId("tournament-1"),
            "Worlds 2026",
            Game.LEAGUE_OF_LEGENDS,
            LocalDate.of(2026, 10, 1),
            LocalDate.of(2026, 11, 5));

    Match match =
        Match.create(
            new MatchId("match-1"),
            team1,
            team2,
            tournament,
            Game.LEAGUE_OF_LEGENDS,
            Instant.parse("2026-10-10T18:00:00Z"));

    when(matchRepository.findById(new MatchId("match-1"))).thenReturn(Optional.of(match));
    when(betRepository.sumAmountByMatch(match)).thenReturn(1000L);
    when(betRepository.sumAmountByMatchAndSelectedTeam(match, team1)).thenReturn(900L);
    when(betRepository.sumAmountByMatchAndSelectedTeam(match, team2)).thenReturn(100L);
    when(coefficientCalculator.calculate(1000L, 900L)).thenReturn(1.2);
    when(coefficientCalculator.calculate(1000L, 100L)).thenReturn(6.0);

    final var result = handler.handle(new MatchId("match-1"));

    assertEquals("match-1", result.matchId());

    assertEquals("team-1", result.team1().id());
    assertEquals("T1", result.team1().name());
    assertEquals(1.2, result.team1().coefficient());

    assertEquals("team-2", result.team2().id());
    assertEquals("Gen.G", result.team2().name());
    assertEquals(6.0, result.team2().coefficient());
  }
}

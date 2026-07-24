package org.underdogs.matches.application.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.underdogs.matches.application.gateways.MatchRepository;
import org.underdogs.matches.application.models.CreateMatchRequest;
import org.underdogs.matches.domain.Match;
import org.underdogs.matches.domain.MatchId;
import org.underdogs.shared.DomainIdGenerator;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.shared.error.BusinessException;
import org.underdogs.teams.application.gateways.TeamRepository;
import org.underdogs.teams.domain.Game;
import org.underdogs.teams.domain.Team;
import org.underdogs.teams.domain.TeamId;
import org.underdogs.tournaments.application.gateways.TournamentRepository;
import org.underdogs.tournaments.domain.Tournament;
import org.underdogs.tournaments.domain.TournamentId;

@ExtendWith(MockitoExtension.class)
class CreateMatchHandlerTest {

  @Mock private MatchRepository matchRepository;

  @Mock private TeamRepository teamRepository;

  @Mock private TournamentRepository tournamentRepository;

  @Mock private DomainIdGenerator domainIdGenerator;

  private CreateMatchHandler handler;

  @BeforeEach
  void setUp() {
    handler =
        new CreateMatchHandler(
            matchRepository, teamRepository, tournamentRepository, domainIdGenerator);
  }

  @Test
  void shouldCreateMatchSuccessfully() {
    Team team1 = Team.create(new TeamId("team-1"), "T1", "T1", Game.LEAGUE_OF_LEGENDS);
    Team team2 = Team.create(new TeamId("team-2"), "Gen.G", "GEN", Game.LEAGUE_OF_LEGENDS);
    Tournament tournament =
        Tournament.create(
            new TournamentId("tournament-1"),
            "Worlds 2026",
            Game.LEAGUE_OF_LEGENDS,
            LocalDate.of(2026, 10, 1),
            LocalDate.of(2026, 11, 5));

    CreateMatchRequest request =
        new CreateMatchRequest(
            "team-1",
            "team-2",
            "tournament-1",
            Game.LEAGUE_OF_LEGENDS,
            LocalDateTime.of(2026, 10, 10, 18, 0));

    when(teamRepository.findById(new TeamId("team-1"))).thenReturn(Optional.of(team1));
    when(teamRepository.findById(new TeamId("team-2"))).thenReturn(Optional.of(team2));
    when(tournamentRepository.findById(new TournamentId("tournament-1")))
        .thenReturn(Optional.of(tournament));
    when(domainIdGenerator.generate()).thenReturn("match-id-123");

    MatchId result = handler.handle(request);

    assertEquals("match-id-123", result.value());
    verify(matchRepository).save(any(Match.class));
  }

  @Test
  void shouldThrowWhenTournamentNotFound() {
    Team team1 = Team.create(new TeamId("team-1"), "T1", "T1", Game.LEAGUE_OF_LEGENDS);
    Team team2 = Team.create(new TeamId("team-2"), "Gen.G", "GEN", Game.LEAGUE_OF_LEGENDS);

    CreateMatchRequest request =
        new CreateMatchRequest(
            "team-1",
            "team-2",
            "tournament-1",
            Game.LEAGUE_OF_LEGENDS,
            LocalDateTime.of(2026, 10, 10, 18, 0));

    when(teamRepository.findById(new TeamId("team-1"))).thenReturn(Optional.of(team1));
    when(teamRepository.findById(new TeamId("team-2"))).thenReturn(Optional.of(team2));
    when(tournamentRepository.findById(new TournamentId("tournament-1")))
        .thenReturn(Optional.empty());

    BusinessException exception =
        assertThrows(BusinessException.class, () -> handler.handle(request));

    assertEquals(BusinessErrorCodes.TOURNAMENT_NOT_FOUND, exception.getCode());
    verify(matchRepository, never()).save(any());
  }

  @Test
  void shouldThrowWhenTeamsAreTheSame() {
    Team team1 = Team.create(new TeamId("team-1"), "T1", "T1", Game.LEAGUE_OF_LEGENDS);
    Tournament tournament =
        Tournament.create(
            new TournamentId("tournament-1"),
            "Worlds 2026",
            Game.LEAGUE_OF_LEGENDS,
            LocalDate.of(2026, 10, 1),
            LocalDate.of(2026, 11, 5));

    CreateMatchRequest request =
        new CreateMatchRequest(
            "team-1",
            "team-1",
            "tournament-1",
            Game.LEAGUE_OF_LEGENDS,
            LocalDateTime.of(2026, 10, 10, 18, 0));

    when(domainIdGenerator.generate()).thenReturn("match-id-123");
    when(teamRepository.findById(any(TeamId.class))).thenReturn(Optional.of(team1));
    when(tournamentRepository.findById(any(TournamentId.class)))
        .thenReturn(Optional.of(tournament));

    BusinessException exception =
        assertThrows(BusinessException.class, () -> handler.handle(request));

    assertEquals(BusinessErrorCodes.INVALID_MATCH_TEAMS, exception.getCode());
    verify(matchRepository, never()).save(any());
  }

  @Test
  void shouldThrowWhenTeamGameDoesNotMatchMatchGame() {
    Team team1 = Team.create(new TeamId("team-1"), "T1", "T1", Game.VALORANT);
    Team team2 = Team.create(new TeamId("team-2"), "Gen.G", "GEN", Game.LEAGUE_OF_LEGENDS);
    Tournament tournament =
        Tournament.create(
            new TournamentId("tournament-1"),
            "Worlds 2026",
            Game.LEAGUE_OF_LEGENDS,
            LocalDate.of(2026, 10, 1),
            LocalDate.of(2026, 11, 5));

    CreateMatchRequest request =
        new CreateMatchRequest(
            "team-1",
            "team-2",
            "tournament-1",
            Game.LEAGUE_OF_LEGENDS,
            LocalDateTime.of(2026, 10, 10, 18, 0));

    when(teamRepository.findById(new TeamId("team-1"))).thenReturn(Optional.of(team1));
    when(teamRepository.findById(new TeamId("team-2"))).thenReturn(Optional.of(team2));
    when(tournamentRepository.findById(new TournamentId("tournament-1")))
        .thenReturn(Optional.of(tournament));
    when(domainIdGenerator.generate()).thenReturn("match-id-123");

    BusinessException exception =
        assertThrows(BusinessException.class, () -> handler.handle(request));

    assertEquals(BusinessErrorCodes.INVALID_MATCH_GAME, exception.getCode());
    verify(matchRepository, never()).save(any());
  }

  @Test
  void shouldThrowWhenScheduledAtIsOutsideTournamentPeriod() {
    Team team1 = Team.create(new TeamId("team-1"), "T1", "T1", Game.LEAGUE_OF_LEGENDS);
    Team team2 = Team.create(new TeamId("team-2"), "Gen.G", "GEN", Game.LEAGUE_OF_LEGENDS);
    Tournament tournament =
        Tournament.create(
            new TournamentId("tournament-1"),
            "Worlds 2026",
            Game.LEAGUE_OF_LEGENDS,
            LocalDate.of(2026, 10, 1),
            LocalDate.of(2026, 11, 5));

    CreateMatchRequest request =
        new CreateMatchRequest(
            "team-1",
            "team-2",
            "tournament-1",
            Game.LEAGUE_OF_LEGENDS,
            LocalDateTime.of(2026, 12, 1, 18, 0));

    when(teamRepository.findById(new TeamId("team-1"))).thenReturn(Optional.of(team1));
    when(teamRepository.findById(new TeamId("team-2"))).thenReturn(Optional.of(team2));
    when(tournamentRepository.findById(new TournamentId("tournament-1")))
        .thenReturn(Optional.of(tournament));
    when(domainIdGenerator.generate()).thenReturn("match-id-123");

    BusinessException exception =
        assertThrows(BusinessException.class, () -> handler.handle(request));

    assertEquals(BusinessErrorCodes.MATCH_DATE_OUTSIDE_TOURNAMENT, exception.getCode());
    verify(matchRepository, never()).save(any());
  }
}

package org.underdogs.matches.application.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.underdogs.bets.application.usecases.ResolveMatchBets;
import org.underdogs.matches.application.gateways.MatchRepository;
import org.underdogs.matches.application.models.UpdateMatchRequest;
import org.underdogs.matches.domain.Match;
import org.underdogs.matches.domain.MatchId;
import org.underdogs.matches.domain.MatchStatus;
import org.underdogs.shared.TimeProvider;
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
class UpdateMatchHandlerTest {

  @Mock private MatchRepository matchRepository;

  @Mock private TeamRepository teamRepository;

  @Mock private TournamentRepository tournamentRepository;

  @Mock private TimeProvider timeProvider;

  @Mock private ResolveMatchBets resolveMatchBets;

  private UpdateMatchHandler handler;

  private Tournament createTournament() {
    return Tournament.create(
        new TournamentId("tournament-1"),
        "Worlds 2026",
        Game.LEAGUE_OF_LEGENDS,
        LocalDate.of(2026, 10, 1),
        LocalDate.of(2026, 11, 5));
  }

  @BeforeEach
  void setUp() {
    handler =
        new UpdateMatchHandler(
            matchRepository, teamRepository, tournamentRepository, timeProvider, resolveMatchBets);
  }

  @Test
  void shouldUpdateMatchToFinishedSuccessfully() {
    Team team1 = Team.create(new TeamId("team-1"), "T1", "T1", Game.LEAGUE_OF_LEGENDS);
    Team team2 = Team.create(new TeamId("team-2"), "Gen.G", "GEN", Game.LEAGUE_OF_LEGENDS);
    Tournament tournament =
        Tournament.create(
            new TournamentId("tournament-1"),
            "Worlds 2026",
            Game.LEAGUE_OF_LEGENDS,
            LocalDate.of(2026, 10, 1),
            LocalDate.of(2026, 11, 5));

    MatchId matchId = new MatchId("match-1");
    Match match =
        Match.create(
            matchId,
            team1,
            team2,
            tournament,
            Game.LEAGUE_OF_LEGENDS,
            LocalDateTime.of(2026, 10, 10, 18, 0));

    UpdateMatchRequest request =
        new UpdateMatchRequest(null, null, null, null, null, MatchStatus.FINISHED, 2, 1, "team-1");

    when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
    when(teamRepository.findById(new TeamId("team-1"))).thenReturn(Optional.of(team1));

    handler.handle(matchId, request);

    verify(matchRepository).save(match);
    assertEquals(MatchStatus.FINISHED, match.getStatus());
    assertEquals(2, match.getTeam1Score());
    assertEquals(1, match.getTeam2Score());
    assertEquals(team1, match.getWinner());
  }

  @Test
  void shouldThrowWhenMatchNotFound() {
    MatchId matchId = new MatchId("match-1");

    UpdateMatchRequest request =
        new UpdateMatchRequest(null, null, null, null, null, MatchStatus.FINISHED, 2, 1, "team-1");

    when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

    BusinessException exception =
        assertThrows(BusinessException.class, () -> handler.handle(matchId, request));

    assertEquals(BusinessErrorCodes.MATCH_NOT_FOUND, exception.getCode());
    verify(matchRepository, never()).save(any());
  }

  @Test
  void shouldThrowWhenWinnerIsNotPartOfMatch() {
    Team team1 = Team.create(new TeamId("team-1"), "T1", "T1", Game.LEAGUE_OF_LEGENDS);
    Team team2 = Team.create(new TeamId("team-2"), "Gen.G", "GEN", Game.LEAGUE_OF_LEGENDS);
    Team outsider = Team.create(new TeamId("team-3"), "G2", "G2", Game.LEAGUE_OF_LEGENDS);

    Tournament tournament =
        Tournament.create(
            new TournamentId("tournament-1"),
            "Worlds 2026",
            Game.LEAGUE_OF_LEGENDS,
            LocalDate.of(2026, 10, 1),
            LocalDate.of(2026, 11, 5));

    MatchId matchId = new MatchId("match-1");
    Match match =
        Match.create(
            matchId,
            team1,
            team2,
            tournament,
            Game.LEAGUE_OF_LEGENDS,
            LocalDateTime.of(2026, 10, 10, 18, 0));

    UpdateMatchRequest request =
        new UpdateMatchRequest(null, null, null, null, null, MatchStatus.FINISHED, 2, 1, "team-3");

    when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
    when(teamRepository.findById(new TeamId("team-3"))).thenReturn(Optional.of(outsider));

    BusinessException exception =
        assertThrows(BusinessException.class, () -> handler.handle(matchId, request));

    assertEquals(BusinessErrorCodes.INVALID_MATCH_WINNER, exception.getCode());
    verify(matchRepository, never()).save(any());
  }

  @Test
  void shouldUpdateMatchStatusToLive() {
    Team team1 = Team.create(new TeamId("team-1"), "T1", "T1", Game.LEAGUE_OF_LEGENDS);
    Team team2 = Team.create(new TeamId("team-2"), "Gen.G", "GEN", Game.LEAGUE_OF_LEGENDS);

    Tournament tournament =
        Tournament.create(
            new TournamentId("tournament-1"),
            "Worlds 2026",
            Game.LEAGUE_OF_LEGENDS,
            LocalDate.of(2026, 10, 1),
            LocalDate.of(2026, 11, 5));

    MatchId matchId = new MatchId("match-1");
    Match match =
        Match.create(
            matchId,
            team1,
            team2,
            tournament,
            Game.LEAGUE_OF_LEGENDS,
            LocalDateTime.of(2026, 10, 10, 18, 0));

    UpdateMatchRequest request =
        new UpdateMatchRequest(null, null, null, null, null, MatchStatus.LIVE, null, null, null);

    when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
    when(timeProvider.now()).thenReturn(Instant.parse("2026-03-21T10:00:00Z"));

    handler.handle(matchId, request);

    assertEquals(MatchStatus.LIVE, match.getStatus());
    assertEquals(Instant.parse("2026-03-21T10:00:00Z"), match.getLiveStartedAt());

    verify(matchRepository).save(match);
  }

  @Test
  void shouldThrowWhenLiveMatchGoesBackToScheduled() {
    Team team1 = Team.create(new TeamId("team-1"), "T1", "T1", Game.LEAGUE_OF_LEGENDS);
    Team team2 = Team.create(new TeamId("team-2"), "Gen.G", "GEN", Game.LEAGUE_OF_LEGENDS);
    Tournament tournament = createTournament();

    Match match =
        Match.create(
            new MatchId("match-1"),
            team1,
            team2,
            tournament,
            Game.LEAGUE_OF_LEGENDS,
            LocalDateTime.of(2026, 10, 10, 18, 0));

    match.startLive(Instant.parse("2026-03-21T10:00:00Z"));

    UpdateMatchRequest request =
        new UpdateMatchRequest(
            null, null, null, null, null, MatchStatus.SCHEDULED, null, null, null);

    when(matchRepository.findById(new MatchId("match-1"))).thenReturn(Optional.of(match));

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> handler.handle(new MatchId("match-1"), request));

    assertEquals(BusinessErrorCodes.INVALID_MATCH_STATUS_TRANSITION, exception.getCode());

    verify(matchRepository, never()).save(any());
    verify(resolveMatchBets, never()).handle(any());
  }

  @Test
  void shouldThrowWhenFinishedMatchChangesStatus() {
    Team team1 = Team.create(new TeamId("team-1"), "T1", "T1", Game.LEAGUE_OF_LEGENDS);
    Team team2 = Team.create(new TeamId("team-2"), "Gen.G", "GEN", Game.LEAGUE_OF_LEGENDS);
    Tournament tournament = createTournament();

    Match match =
        Match.create(
            new MatchId("match-1"),
            team1,
            team2,
            tournament,
            Game.LEAGUE_OF_LEGENDS,
            LocalDateTime.of(2026, 10, 10, 18, 0));

    match.update(null, null, null, null, null, MatchStatus.FINISHED, 2, 1, team1);

    UpdateMatchRequest request =
        new UpdateMatchRequest(null, null, null, null, null, MatchStatus.LIVE, null, null, null);

    when(matchRepository.findById(new MatchId("match-1"))).thenReturn(Optional.of(match));

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> handler.handle(new MatchId("match-1"), request));

    assertEquals(BusinessErrorCodes.INVALID_MATCH_STATUS_TRANSITION, exception.getCode());

    verify(matchRepository, never()).save(any());
    verify(resolveMatchBets, never()).handle(any());
  }

  @Test
  void shouldThrowWhenCancelledMatchChangesStatus() {
    Team team1 = Team.create(new TeamId("team-1"), "T1", "T1", Game.LEAGUE_OF_LEGENDS);
    Team team2 = Team.create(new TeamId("team-2"), "Gen.G", "GEN", Game.LEAGUE_OF_LEGENDS);
    Tournament tournament = createTournament();

    Match match =
        Match.create(
            new MatchId("match-1"),
            team1,
            team2,
            tournament,
            Game.LEAGUE_OF_LEGENDS,
            LocalDateTime.of(2026, 10, 10, 18, 0));

    match.update(null, null, null, null, null, MatchStatus.CANCELLED, null, null, null);

    UpdateMatchRequest request =
        new UpdateMatchRequest(
            null, null, null, null, null, MatchStatus.SCHEDULED, null, null, null);

    when(matchRepository.findById(new MatchId("match-1"))).thenReturn(Optional.of(match));

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> handler.handle(new MatchId("match-1"), request));

    assertEquals(BusinessErrorCodes.INVALID_MATCH_STATUS_TRANSITION, exception.getCode());

    verify(matchRepository, never()).save(any());
    verify(resolveMatchBets, never()).handle(any());
  }

  @Test
  void shouldResolveBetsWhenMatchBecomesFinished() {
    Team team1 = Team.create(new TeamId("team-1"), "T1", "T1", Game.LEAGUE_OF_LEGENDS);
    Team team2 = Team.create(new TeamId("team-2"), "Gen.G", "GEN", Game.LEAGUE_OF_LEGENDS);
    Tournament tournament = createTournament();

    Match match =
        Match.create(
            new MatchId("match-1"),
            team1,
            team2,
            tournament,
            Game.LEAGUE_OF_LEGENDS,
            LocalDateTime.of(2026, 10, 10, 18, 0));

    UpdateMatchRequest request =
        new UpdateMatchRequest(null, null, null, null, null, MatchStatus.FINISHED, 2, 1, "team-1");

    when(matchRepository.findById(new MatchId("match-1"))).thenReturn(Optional.of(match));
    when(teamRepository.findById(new TeamId("team-1"))).thenReturn(Optional.of(team1));

    handler.handle(new MatchId("match-1"), request);

    verify(matchRepository).save(match);
    verify(resolveMatchBets).handle(match);
  }

  @Test
  void shouldResolveBetsWhenMatchBecomesCancelled() {
    Team team1 = Team.create(new TeamId("team-1"), "T1", "T1", Game.LEAGUE_OF_LEGENDS);
    Team team2 = Team.create(new TeamId("team-2"), "Gen.G", "GEN", Game.LEAGUE_OF_LEGENDS);
    Tournament tournament = createTournament();

    Match match =
        Match.create(
            new MatchId("match-1"),
            team1,
            team2,
            tournament,
            Game.LEAGUE_OF_LEGENDS,
            LocalDateTime.of(2026, 10, 10, 18, 0));

    UpdateMatchRequest request =
        new UpdateMatchRequest(
            null, null, null, null, null, MatchStatus.CANCELLED, null, null, null);

    when(matchRepository.findById(new MatchId("match-1"))).thenReturn(Optional.of(match));

    handler.handle(new MatchId("match-1"), request);

    verify(matchRepository).save(match);
    verify(resolveMatchBets).handle(match);
  }

  @Test
  void shouldNotResolveBetsWhenStatusDoesNotBecomeFinal() {
    Team team1 = Team.create(new TeamId("team-1"), "T1", "T1", Game.LEAGUE_OF_LEGENDS);
    Team team2 = Team.create(new TeamId("team-2"), "Gen.G", "GEN", Game.LEAGUE_OF_LEGENDS);
    Tournament tournament = createTournament();

    Match match =
        Match.create(
            new MatchId("match-1"),
            team1,
            team2,
            tournament,
            Game.LEAGUE_OF_LEGENDS,
            LocalDateTime.of(2026, 10, 10, 18, 0));

    UpdateMatchRequest request =
        new UpdateMatchRequest(null, null, null, Game.VALORANT, null, null, null, null, null);

    when(matchRepository.findById(new MatchId("match-1"))).thenReturn(Optional.of(match));

    handler.handle(new MatchId("match-1"), request);

    verify(matchRepository).save(match);
    verify(resolveMatchBets, never()).handle(any());
  }
}

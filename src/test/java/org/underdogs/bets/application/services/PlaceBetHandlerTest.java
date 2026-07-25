package org.underdogs.bets.application.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.underdogs.bets.application.gateways.BetRepository;
import org.underdogs.bets.application.models.PlaceBetRequest;
import org.underdogs.bets.domain.Bet;
import org.underdogs.bets.domain.BetId;
import org.underdogs.bets.domain.BetStatus;
import org.underdogs.matches.application.gateways.MatchRepository;
import org.underdogs.matches.domain.Match;
import org.underdogs.matches.domain.MatchId;
import org.underdogs.matches.domain.MatchStatus;
import org.underdogs.shared.DomainIdGenerator;
import org.underdogs.shared.TimeProvider;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.shared.error.BusinessException;
import org.underdogs.teams.application.gateways.TeamRepository;
import org.underdogs.teams.domain.Game;
import org.underdogs.teams.domain.Team;
import org.underdogs.teams.domain.TeamId;
import org.underdogs.tournaments.domain.Tournament;
import org.underdogs.tournaments.domain.TournamentId;
import org.underdogs.users.application.gateways.UserRepository;
import org.underdogs.users.application.usecases.SyncCurrentUser;
import org.underdogs.users.domain.User;

@ExtendWith(MockitoExtension.class)
class PlaceBetHandlerTest {

  @Mock private BetRepository betRepository;

  @Mock private MatchRepository matchRepository;

  @Mock private TeamRepository teamRepository;

  @Mock private UserRepository userRepository;

  @Mock private SyncCurrentUser syncCurrentUser;

  @Mock private DomainIdGenerator domainIdGenerator;

  @Mock private TimeProvider timeProvider;

  @Mock private BetCoefficientCalculator coefficientCalculator;

  @Mock private Jwt jwt;

  private PlaceBetHandler handler;

  @BeforeEach
  void setUp() {
    handler =
        new PlaceBetHandler(
            betRepository,
            matchRepository,
            teamRepository,
            userRepository,
            syncCurrentUser,
            domainIdGenerator,
            timeProvider,
            coefficientCalculator);
  }

  @Test
  void shouldPlaceBetSuccessfully() {
    User user = mock(User.class);

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
            Instant.parse("2026-10-10T18:00:00Z"));

    PlaceBetRequest request = new PlaceBetRequest("match-1", "team-1", 100);

    when(syncCurrentUser.handle(jwt)).thenReturn(user);
    when(matchRepository.findById(any(MatchId.class))).thenReturn(Optional.of(match));
    when(teamRepository.findById(any(TeamId.class))).thenReturn(Optional.of(team1));
    when(betRepository.existsByUserAndMatch(user, match)).thenReturn(false);

    when(betRepository.sumAmountByMatch(match)).thenReturn(1000L);
    when(betRepository.sumAmountByMatchAndSelectedTeam(match, team1)).thenReturn(100L);
    when(coefficientCalculator.calculate(1000L, 100L)).thenReturn(6.0);
    when(coefficientCalculator.calculatePotentialGain(100, 6.0)).thenReturn(600L);

    when(domainIdGenerator.generate()).thenReturn("bet-1");
    when(timeProvider.now()).thenReturn(Instant.parse("2026-03-21T10:00:00Z"));

    BetId result = handler.handle(jwt, request);

    assertEquals("bet-1", result.value());

    verify(user).debitKibbles(100);
    verify(userRepository).save(user);

    ArgumentCaptor<Bet> betCaptor = ArgumentCaptor.forClass(Bet.class);
    verify(betRepository).save(betCaptor.capture());

    Bet savedBet = betCaptor.getValue();

    assertEquals("bet-1", savedBet.getId().value());
    assertEquals(match, savedBet.getMatch());
    assertEquals(team1, savedBet.getSelectedTeam());
    assertEquals(100, savedBet.getAmount());
    assertEquals(6.0, savedBet.getCoefficient());
    assertEquals(600L, savedBet.getPotentialGain());
    assertEquals(BetStatus.PENDING, savedBet.getStatus());
    assertEquals(Instant.parse("2026-03-21T10:00:00Z"), savedBet.getCreatedAt());

    verify(betRepository).sumAmountByMatch(match);
    verify(betRepository).sumAmountByMatchAndSelectedTeam(match, team1);
    verify(coefficientCalculator).calculate(1000L, 100L);
    verify(coefficientCalculator).calculatePotentialGain(100, 6.0);
  }

  @Test
  void shouldThrowWhenMatchNotFound() {
    User user = mock(User.class);
    PlaceBetRequest request = new PlaceBetRequest("match-1", "team-1", 100);

    when(syncCurrentUser.handle(jwt)).thenReturn(user);
    when(matchRepository.findById(any(MatchId.class))).thenReturn(Optional.empty());

    BusinessException exception =
        assertThrows(BusinessException.class, () -> handler.handle(jwt, request));

    assertEquals(BusinessErrorCodes.MATCH_NOT_FOUND, exception.getCode());

    verify(user, never()).debitKibbles(100);
    verify(betRepository, never()).save(any());
  }

  @Test
  void shouldThrowWhenMatchIsNotOpenForBets() {
    User user = mock(User.class);

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
            Instant.parse("2026-10-10T18:00:00Z"));

    match.update(null, null, null, null, null, MatchStatus.LIVE, null, null, null);

    PlaceBetRequest request = new PlaceBetRequest("match-1", "team-1", 100);

    when(syncCurrentUser.handle(jwt)).thenReturn(user);
    when(matchRepository.findById(any(MatchId.class))).thenReturn(Optional.of(match));
    when(timeProvider.now()).thenReturn(Instant.parse("2026-03-21T10:00:00Z"));

    BusinessException exception =
        assertThrows(BusinessException.class, () -> handler.handle(jwt, request));

    assertEquals(BusinessErrorCodes.MATCH_NOT_OPEN_FOR_BETS, exception.getCode());

    verify(user, never()).debitKibbles(100);
    verify(betRepository, never()).save(any());
  }

  @Test
  void shouldThrowWhenSelectedTeamIsNotPartOfMatch() {
    User user = mock(User.class);

    Team team1 = Team.create(new TeamId("team-1"), "T1", "T1", Game.LEAGUE_OF_LEGENDS);
    Team team2 = Team.create(new TeamId("team-2"), "Gen.G", "GEN", Game.LEAGUE_OF_LEGENDS);
    Team outsider = Team.create(new TeamId("team-3"), "G2", "G2", Game.LEAGUE_OF_LEGENDS);
    Tournament tournament = createTournament();

    Match match =
        Match.create(
            new MatchId("match-1"),
            team1,
            team2,
            tournament,
            Game.LEAGUE_OF_LEGENDS,
            Instant.parse("2026-10-10T18:00:00Z"));

    PlaceBetRequest request = new PlaceBetRequest("match-1", "team-3", 100);

    when(syncCurrentUser.handle(jwt)).thenReturn(user);
    when(matchRepository.findById(any(MatchId.class))).thenReturn(Optional.of(match));
    when(teamRepository.findById(any(TeamId.class))).thenReturn(Optional.of(outsider));

    BusinessException exception =
        assertThrows(BusinessException.class, () -> handler.handle(jwt, request));

    assertEquals(BusinessErrorCodes.TEAM_NOT_IN_MATCH, exception.getCode());

    verify(user, never()).debitKibbles(100);
    verify(betRepository, never()).save(any());
  }

  @Test
  void shouldThrowWhenBetAlreadyExists() {
    User user = mock(User.class);

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
            Instant.parse("2026-10-10T18:00:00Z"));

    PlaceBetRequest request = new PlaceBetRequest("match-1", "team-1", 100);

    when(syncCurrentUser.handle(jwt)).thenReturn(user);
    when(matchRepository.findById(any(MatchId.class))).thenReturn(Optional.of(match));
    when(teamRepository.findById(any(TeamId.class))).thenReturn(Optional.of(team1));
    when(betRepository.existsByUserAndMatch(user, match)).thenReturn(true);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> handler.handle(jwt, request));

    assertEquals(BusinessErrorCodes.BET_ALREADY_EXISTS, exception.getCode());

    verify(user, never()).debitKibbles(100);
    verify(betRepository, never()).save(any());
  }

  @Test
  void shouldThrowWhenUserHasNotEnoughKibbles() {
    User user = mock(User.class);

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
            Instant.parse("2026-10-10T18:00:00Z"));

    PlaceBetRequest request = new PlaceBetRequest("match-1", "team-1", 100);

    when(syncCurrentUser.handle(jwt)).thenReturn(user);
    when(matchRepository.findById(any(MatchId.class))).thenReturn(Optional.of(match));
    when(teamRepository.findById(any(TeamId.class))).thenReturn(Optional.of(team1));
    when(betRepository.existsByUserAndMatch(user, match)).thenReturn(false);

    when(betRepository.sumAmountByMatch(match)).thenReturn(0L);
    when(betRepository.sumAmountByMatchAndSelectedTeam(match, team1)).thenReturn(0L);
    when(coefficientCalculator.calculate(0L, 0L)).thenReturn(2.0);
    when(coefficientCalculator.calculatePotentialGain(100, 2.0)).thenReturn(200L);

    doThrow(new BusinessException(BusinessErrorCodes.INSUFFICIENT_KIBBLES, "Not enough kibbles"))
        .when(user)
        .debitKibbles(100);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> handler.handle(jwt, request));

    assertEquals(BusinessErrorCodes.INSUFFICIENT_KIBBLES, exception.getCode());

    verify(userRepository, never()).save(any());
    verify(betRepository, never()).save(any());
  }

  @Test
  void shouldAllowBetWhenMatchIsLiveForLessThanFiveMinutes() {
    User user = mock(User.class);

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
            Instant.parse("2026-10-10T18:00:00Z"));

    match.startLive(Instant.parse("2026-03-21T10:00:00Z"));

    PlaceBetRequest request = new PlaceBetRequest("match-1", "team-1", 100);

    when(syncCurrentUser.handle(jwt)).thenReturn(user);
    when(matchRepository.findById(any(MatchId.class))).thenReturn(Optional.of(match));
    when(teamRepository.findById(any(TeamId.class))).thenReturn(Optional.of(team1));
    when(betRepository.existsByUserAndMatch(user, match)).thenReturn(false);

    when(betRepository.sumAmountByMatch(match)).thenReturn(0L);
    when(betRepository.sumAmountByMatchAndSelectedTeam(match, team1)).thenReturn(0L);
    when(coefficientCalculator.calculate(0L, 0L)).thenReturn(2.0);
    when(coefficientCalculator.calculatePotentialGain(100, 2.0)).thenReturn(200L);

    when(domainIdGenerator.generate()).thenReturn("bet-1");
    when(timeProvider.now()).thenReturn(Instant.parse("2026-03-21T10:04:59Z"));

    BetId result = handler.handle(jwt, request);

    assertEquals("bet-1", result.value());

    verify(user).debitKibbles(100);

    ArgumentCaptor<Bet> betCaptor = ArgumentCaptor.forClass(Bet.class);
    verify(betRepository).save(betCaptor.capture());

    Bet savedBet = betCaptor.getValue();

    assertEquals(2.0, savedBet.getCoefficient());
    assertEquals(200L, savedBet.getPotentialGain());
    assertEquals(BetStatus.PENDING, savedBet.getStatus());
  }

  @Test
  void shouldRejectBetWhenMatchIsLiveForMoreThanFiveMinutes() {
    User user = mock(User.class);

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
            Instant.parse("2026-10-10T18:00:00Z"));

    match.startLive(Instant.parse("2026-03-21T10:00:00Z"));

    PlaceBetRequest request = new PlaceBetRequest("match-1", "team-1", 100);

    when(syncCurrentUser.handle(jwt)).thenReturn(user);
    when(matchRepository.findById(any(MatchId.class))).thenReturn(Optional.of(match));
    when(timeProvider.now()).thenReturn(Instant.parse("2026-03-21T10:05:01Z"));

    BusinessException exception =
        assertThrows(BusinessException.class, () -> handler.handle(jwt, request));

    assertEquals(BusinessErrorCodes.MATCH_NOT_OPEN_FOR_BETS, exception.getCode());

    verify(user, never()).debitKibbles(100);
    verify(betRepository, never()).save(any());
  }

  private Tournament createTournament() {
    return Tournament.create(
        new TournamentId("tournament-1"),
        "Worlds 2026",
        Game.LEAGUE_OF_LEGENDS,
        LocalDate.of(2026, 10, 1),
        LocalDate.of(2026, 11, 5));
  }
}

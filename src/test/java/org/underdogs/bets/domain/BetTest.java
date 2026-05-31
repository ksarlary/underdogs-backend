package org.underdogs.bets.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.underdogs.matches.domain.Match;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.shared.error.BusinessException;
import org.underdogs.teams.domain.Team;
import org.underdogs.users.domain.User;

class BetTest {

  @Test
  void shouldCreatePendingBet() {
    User user = mock(User.class);
    Match match = mock(Match.class);
    Team selectedTeam = mock(Team.class);
    Instant now = Instant.parse("2026-03-21T10:00:00Z");

    Bet bet = Bet.create(new BetId("bet-1"), user, match, selectedTeam, 100, 2.5, 250, now);

    assertEquals("bet-1", bet.getId().value());
    assertEquals(user, bet.getUser());
    assertEquals(match, bet.getMatch());
    assertEquals(selectedTeam, bet.getSelectedTeam());
    assertEquals(100, bet.getAmount());
    assertEquals(2.5, bet.getCoefficient());
    assertEquals(250, bet.getPotentialGain());
    assertEquals(BetStatus.PENDING, bet.getStatus());
    assertEquals(now, bet.getCreatedAt());
    assertEquals(null, bet.getResolvedAt());
  }

  @Test
  void shouldThrowWhenAmountIsZero() {
    User user = mock(User.class);
    Match match = mock(Match.class);
    Team selectedTeam = mock(Team.class);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () ->
                Bet.create(
                    new BetId("bet-1"),
                    user,
                    match,
                    selectedTeam,
                    0,
                    2.0,
                    200,
                    Instant.parse("2026-03-21T10:00:00Z")));

    assertEquals(BusinessErrorCodes.INVALID_BET_AMOUNT, exception.getCode());
  }

  @Test
  void shouldThrowWhenAmountIsNegative() {
    User user = mock(User.class);
    Match match = mock(Match.class);
    Team selectedTeam = mock(Team.class);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () ->
                Bet.create(
                    new BetId("bet-1"),
                    user,
                    match,
                    selectedTeam,
                    -50,
                    2.0,
                    200,
                    Instant.parse("2026-03-21T10:00:00Z")));

    assertEquals(BusinessErrorCodes.INVALID_BET_AMOUNT, exception.getCode());
  }

  @Test
  void shouldThrowWhenUserIsNull() {
    Match match = mock(Match.class);
    Team selectedTeam = mock(Team.class);

    assertThrows(
        IllegalArgumentException.class,
        () ->
            Bet.create(
                new BetId("bet-1"),
                null,
                match,
                selectedTeam,
                100,
                2.0,
                200,
                Instant.parse("2026-03-21T10:00:00Z")));
  }

  @Test
  void shouldThrowWhenMatchIsNull() {
    User user = mock(User.class);
    Team selectedTeam = mock(Team.class);

    assertThrows(
        IllegalArgumentException.class,
        () ->
            Bet.create(
                new BetId("bet-1"),
                user,
                null,
                selectedTeam,
                100,
                2.0,
                200,
                Instant.parse("2026-03-21T10:00:00Z")));
  }

  @Test
  void shouldThrowWhenSelectedTeamIsNull() {
    User user = mock(User.class);
    Match match = mock(Match.class);

    assertThrows(
        IllegalArgumentException.class,
        () ->
            Bet.create(
                new BetId("bet-1"),
                user,
                match,
                null,
                100,
                2.0,
                200,
                Instant.parse("2026-03-21T10:00:00Z")));
  }

  @Test
  void shouldThrowWhenCoefficientIsLowerThanOne() {
    User user = mock(User.class);
    Match match = mock(Match.class);
    Team selectedTeam = mock(Team.class);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () ->
                Bet.create(
                    new BetId("bet-1"),
                    user,
                    match,
                    selectedTeam,
                    100,
                    0.9,
                    150,
                    Instant.parse("2026-03-21T10:00:00Z")));

    assertEquals(BusinessErrorCodes.INVALID_BET_COEFFICIENT, exception.getCode());
  }

  @Test
  void shouldThrowWhenPotentialGainIsLowerThanAmount() {
    User user = mock(User.class);
    Match match = mock(Match.class);
    Team selectedTeam = mock(Team.class);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () ->
                Bet.create(
                    new BetId("bet-1"),
                    user,
                    match,
                    selectedTeam,
                    100,
                    1.5,
                    99,
                    Instant.parse("2026-03-21T10:00:00Z")));

    assertEquals(BusinessErrorCodes.INVALID_POTENTIAL_GAIN, exception.getCode());
  }
}

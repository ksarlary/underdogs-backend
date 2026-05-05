package org.underdogs.bets.application.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.underdogs.bets.application.gateways.BetRepository;
import org.underdogs.bets.domain.Bet;
import org.underdogs.users.application.usecases.SyncCurrentUser;
import org.underdogs.users.domain.User;

@ExtendWith(MockitoExtension.class)
class SearchCurrentUserBetsHandlerTest {

  @Mock private BetRepository betRepository;

  @Mock private SyncCurrentUser syncCurrentUser;

  @Mock private Jwt jwt;

  private SearchCurrentUserBetsHandler handler;

  @BeforeEach
  void setUp() {
    handler = new SearchCurrentUserBetsHandler(betRepository, syncCurrentUser);
  }

  @Test
  void shouldReturnCurrentUserBets() {
    User user = mock(User.class);
    Bet bet1 = mock(Bet.class);
    Bet bet2 = mock(Bet.class);

    when(syncCurrentUser.handle(jwt)).thenReturn(user);
    when(betRepository.findByUser(user)).thenReturn(List.of(bet1, bet2));

    List<Bet> result = handler.handle(jwt);

    assertEquals(2, result.size());
    assertEquals(List.of(bet1, bet2), result);

    verify(syncCurrentUser).handle(jwt);
    verify(betRepository).findByUser(user);
  }
}

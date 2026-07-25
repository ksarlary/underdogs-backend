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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
    PageRequest pageable = PageRequest.of(0, 10);
    Page<Bet> page = new PageImpl<>(List.of(bet1, bet2), pageable, 2);

    when(syncCurrentUser.handle(jwt)).thenReturn(user);
    when(betRepository.findByUser(user, null, pageable)).thenReturn(page);

    Page<Bet> result = handler.handle(jwt, null, pageable);

    assertEquals(2, result.getContent().size());
    assertEquals(List.of(bet1, bet2), result.getContent());

    verify(syncCurrentUser).handle(jwt);
    verify(betRepository).findByUser(user, null, pageable);
  }
}

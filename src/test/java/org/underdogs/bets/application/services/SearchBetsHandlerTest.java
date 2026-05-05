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
import org.underdogs.bets.application.gateways.BetRepository;
import org.underdogs.bets.domain.Bet;

@ExtendWith(MockitoExtension.class)
class SearchBetsHandlerTest {

  @Mock private BetRepository betRepository;

  private SearchBetsHandler handler;

  @BeforeEach
  void setUp() {
    handler = new SearchBetsHandler(betRepository);
  }

  @Test
  void shouldReturnAllBets() {
    Bet bet1 = mock(Bet.class);
    Bet bet2 = mock(Bet.class);

    when(betRepository.findAll()).thenReturn(List.of(bet1, bet2));

    List<Bet> result = handler.handle();

    assertEquals(2, result.size());
    assertEquals(List.of(bet1, bet2), result);

    verify(betRepository).findAll();
  }
}

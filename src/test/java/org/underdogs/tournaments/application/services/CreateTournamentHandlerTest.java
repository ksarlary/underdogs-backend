package org.underdogs.tournaments.application.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.underdogs.shared.DomainIdGenerator;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.shared.error.BusinessException;
import org.underdogs.teams.domain.Game;
import org.underdogs.tournaments.application.gateways.TournamentRepository;
import org.underdogs.tournaments.application.models.CreateTournamentRequest;
import org.underdogs.tournaments.domain.Tournament;
import org.underdogs.tournaments.domain.TournamentId;

@ExtendWith(MockitoExtension.class)
class CreateTournamentHandlerTest {

  @Mock private TournamentRepository tournamentRepository;

  @Mock private DomainIdGenerator domainIdGenerator;

  private CreateTournamentHandler handler;

  @BeforeEach
  void setUp() {
    handler = new CreateTournamentHandler(tournamentRepository, domainIdGenerator);
  }

  @Test
  void shouldCreateTournamentSuccessfully() {
    CreateTournamentRequest request =
        new CreateTournamentRequest(
            "Worlds 2026",
            Game.LEAGUE_OF_LEGENDS,
            LocalDate.of(2026, 10, 1),
            LocalDate.of(2026, 11, 5));

    when(tournamentRepository.findByName("Worlds 2026")).thenReturn(Optional.empty());
    when(domainIdGenerator.generate()).thenReturn("tournament-id-123");

    TournamentId result = handler.handle(request);

    assertEquals("tournament-id-123", result.value());
    verify(tournamentRepository).save(any(Tournament.class));
  }

  @Test
  void shouldThrowWhenTournamentAlreadyExists() {
    CreateTournamentRequest request =
        new CreateTournamentRequest(
            "Worlds 2026",
            Game.LEAGUE_OF_LEGENDS,
            LocalDate.of(2026, 10, 1),
            LocalDate.of(2026, 11, 5));

    when(tournamentRepository.findByName("Worlds 2026"))
        .thenReturn(Optional.of(mock(Tournament.class)));

    BusinessException exception =
        assertThrows(BusinessException.class, () -> handler.handle(request));

    assertEquals(BusinessErrorCodes.TOURNAMENT_ALREADY_EXISTS, exception.getCode());
    verify(tournamentRepository, never()).save(any());
  }

  @Test
  void shouldThrowWhenEndDateIsBeforeStartDate() {
    CreateTournamentRequest request =
        new CreateTournamentRequest(
            "Worlds 2026",
            Game.LEAGUE_OF_LEGENDS,
            LocalDate.of(2026, 11, 5),
            LocalDate.of(2026, 10, 1));

    when(tournamentRepository.findByName("Worlds 2026")).thenReturn(Optional.empty());
    when(domainIdGenerator.generate()).thenReturn("tournament-id-123");

    assertThrows(IllegalArgumentException.class, () -> handler.handle(request));

    verify(tournamentRepository, never()).save(any());
  }
}

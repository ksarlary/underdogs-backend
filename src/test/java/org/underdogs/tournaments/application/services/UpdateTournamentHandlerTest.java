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
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.shared.error.BusinessException;
import org.underdogs.teams.domain.Game;
import org.underdogs.tournaments.application.gateways.TournamentRepository;
import org.underdogs.tournaments.application.models.UpdateTournamentRequest;
import org.underdogs.tournaments.domain.Tournament;
import org.underdogs.tournaments.domain.TournamentId;

@ExtendWith(MockitoExtension.class)
class UpdateTournamentHandlerTest {

  @Mock private TournamentRepository tournamentRepository;

  private UpdateTournamentHandler handler;

  @BeforeEach
  void setUp() {
    handler = new UpdateTournamentHandler(tournamentRepository);
  }

  @Test
  void shouldUpdateTournamentSuccessfully() {
    TournamentId tournamentId = new TournamentId("tournament-id-123");
    Tournament tournament =
        Tournament.create(
            tournamentId,
            "Worlds 2026",
            Game.LEAGUE_OF_LEGENDS,
            LocalDate.of(2026, 10, 1),
            LocalDate.of(2026, 11, 5));

    UpdateTournamentRequest request =
        new UpdateTournamentRequest("World Championship 2026", null, null, null);

    when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
    when(tournamentRepository.findByName("World Championship 2026")).thenReturn(Optional.empty());

    handler.handle(tournamentId, request);

    verify(tournamentRepository).save(tournament);
    assertEquals("World Championship 2026", tournament.getName());
  }

  @Test
  void shouldThrowWhenTournamentNotFound() {
    TournamentId tournamentId = new TournamentId("tournament-id-123");
    UpdateTournamentRequest request =
        new UpdateTournamentRequest("World Championship 2026", null, null, null);

    when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

    BusinessException exception =
        assertThrows(BusinessException.class, () -> handler.handle(tournamentId, request));

    assertEquals(BusinessErrorCodes.TOURNAMENT_NOT_FOUND, exception.getCode());
    verify(tournamentRepository, never()).save(any());
  }

  @Test
  void shouldThrowWhenTournamentNameAlreadyExists() {
    TournamentId tournamentId = new TournamentId("tournament-id-123");
    Tournament tournament =
        Tournament.create(
            tournamentId,
            "Worlds 2026",
            Game.LEAGUE_OF_LEGENDS,
            LocalDate.of(2026, 10, 1),
            LocalDate.of(2026, 11, 5));

    Tournament existing =
        Tournament.create(
            new TournamentId("other-id"),
            "World Championship 2026",
            Game.LEAGUE_OF_LEGENDS,
            LocalDate.of(2026, 10, 1),
            LocalDate.of(2026, 11, 5));

    UpdateTournamentRequest request =
        new UpdateTournamentRequest("World Championship 2026", null, null, null);

    when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
    when(tournamentRepository.findByName("World Championship 2026"))
        .thenReturn(Optional.of(existing));

    BusinessException exception =
        assertThrows(BusinessException.class, () -> handler.handle(tournamentId, request));

    assertEquals(BusinessErrorCodes.TOURNAMENT_ALREADY_EXISTS, exception.getCode());
    verify(tournamentRepository, never()).save(any());
  }
}

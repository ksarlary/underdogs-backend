package org.underdogs.teams.application.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.underdogs.shared.DomainIdGenerator;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.shared.error.BusinessException;
import org.underdogs.teams.application.gateways.TeamRepository;
import org.underdogs.teams.application.models.CreateTeamRequest;
import org.underdogs.teams.domain.Game;
import org.underdogs.teams.domain.Team;
import org.underdogs.teams.domain.TeamId;

@ExtendWith(MockitoExtension.class)
class CreateTeamHandlerTest {

  @Mock private TeamRepository teamRepository;

  @Mock private DomainIdGenerator domainIdGenerator;

  private CreateTeamHandler handler;

  @BeforeEach
  void setUp() {
    handler = new CreateTeamHandler(teamRepository, domainIdGenerator);
  }

  @Test
  void shouldCreateTeamSuccessfully() {
    CreateTeamRequest request = new CreateTeamRequest("T1", "T1", Game.LEAGUE_OF_LEGENDS);

    when(teamRepository.findByName("T1")).thenReturn(Optional.empty());
    when(teamRepository.findByTag("T1")).thenReturn(Optional.empty());
    when(domainIdGenerator.generate()).thenReturn("team-id-123");

    TeamId result = handler.handle(request);

    assertEquals("team-id-123", result.value());
    verify(teamRepository).save(any(Team.class));
  }

  @Test
  void shouldThrowWhenTeamNameAlreadyExists() {
    CreateTeamRequest request = new CreateTeamRequest("T1", "T1", Game.LEAGUE_OF_LEGENDS);

    when(teamRepository.findByName("T1")).thenReturn(Optional.of(mock(Team.class)));

    BusinessException exception =
        assertThrows(BusinessException.class, () -> handler.handle(request));

    assertEquals(BusinessErrorCodes.TEAM_NAME_ALREADY_EXISTS, exception.getCode());
    verify(teamRepository, never()).save(any());
  }

  @Test
  void shouldThrowWhenTeamTagAlreadyExists() {
    CreateTeamRequest request = new CreateTeamRequest("T1", "T1", Game.LEAGUE_OF_LEGENDS);

    when(teamRepository.findByName("T1")).thenReturn(Optional.empty());
    when(teamRepository.findByTag("T1")).thenReturn(Optional.of(mock(Team.class)));

    BusinessException exception =
        assertThrows(BusinessException.class, () -> handler.handle(request));

    assertEquals(BusinessErrorCodes.TEAM_TAG_ALREADY_EXISTS, exception.getCode());
    verify(teamRepository, never()).save(any());
  }
}

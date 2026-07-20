package org.underdogs.players.application.services;

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
import org.underdogs.players.application.gateways.PlayerRepository;
import org.underdogs.players.application.models.CreatePlayerRequest;
import org.underdogs.players.domain.Player;
import org.underdogs.players.domain.PlayerId;
import org.underdogs.shared.DomainIdGenerator;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.shared.error.BusinessException;
import org.underdogs.teams.application.gateways.TeamRepository;
import org.underdogs.teams.domain.Game;
import org.underdogs.teams.domain.Team;
import org.underdogs.teams.domain.TeamId;

@ExtendWith(MockitoExtension.class)
class CreatePlayerHandlerTest {

  @Mock private PlayerRepository playerRepository;

  @Mock private TeamRepository teamRepository;

  @Mock private DomainIdGenerator domainIdGenerator;

  private CreatePlayerHandler handler;

  @BeforeEach
  void setUp() {
    handler = new CreatePlayerHandler(playerRepository, teamRepository, domainIdGenerator);
  }

  @Test
  void shouldCreatePlayerSuccessfully() {
    CreatePlayerRequest request =
        new CreatePlayerRequest("Faker", "Lee Sang-hyeok", "MID", "KR", "team-id-123");

    Team team = Team.create(new TeamId("team-id-123"), "T1", "T1", Game.LEAGUE_OF_LEGENDS);

    when(playerRepository.findByNickname("Faker")).thenReturn(Optional.empty());
    when(teamRepository.findById(new TeamId("team-id-123"))).thenReturn(Optional.of(team));
    when(domainIdGenerator.generate()).thenReturn("player-id-123");

    PlayerId result = handler.handle(request);

    assertEquals("player-id-123", result.value());
    verify(playerRepository).save(any(Player.class));
  }

  @Test
  void shouldThrowWhenPlayerNicknameAlreadyExists() {
    CreatePlayerRequest request =
        new CreatePlayerRequest("Faker", "Lee Sang-hyeok", "MID", "KR", "team-id-123");

    when(playerRepository.findByNickname("Faker")).thenReturn(Optional.of(mock(Player.class)));

    BusinessException exception =
        assertThrows(BusinessException.class, () -> handler.handle(request));

    assertEquals(BusinessErrorCodes.PLAYER_ALREADY_EXISTS, exception.getCode());
    verify(playerRepository, never()).save(any());
  }

  @Test
  void shouldThrowWhenTeamDoesNotExist() {
    CreatePlayerRequest request =
        new CreatePlayerRequest("Faker", "Lee Sang-hyeok", "MID", "KR", "team-id-123");

    when(playerRepository.findByNickname("Faker")).thenReturn(Optional.empty());
    when(teamRepository.findById(new TeamId("team-id-123"))).thenReturn(Optional.empty());

    BusinessException exception =
        assertThrows(BusinessException.class, () -> handler.handle(request));

    assertEquals(BusinessErrorCodes.TEAM_NOT_FOUND, exception.getCode());
    verify(playerRepository, never()).save(any());
  }
}

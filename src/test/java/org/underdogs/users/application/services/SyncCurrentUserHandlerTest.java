package org.underdogs.users.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.underdogs.shared.BirthDateParser;
import org.underdogs.shared.DomainIdGenerator;
import org.underdogs.shared.TimeProvider;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.shared.error.BusinessException;
import org.underdogs.users.application.gateways.UserRepository;
import org.underdogs.users.domain.User;

@ExtendWith(MockitoExtension.class)
class SyncCurrentUserHandlerTest {

  @Mock private UserRepository userRepository;

  @Mock private DomainIdGenerator domainIdGenerator;

  @Mock private TimeProvider timeProvider;

  @Mock private BirthDateParser birthDateParser;

  private SyncCurrentUserHandler handler;

  @BeforeEach
  void setUp() {
    handler =
        new SyncCurrentUserHandler(
            userRepository, domainIdGenerator, timeProvider, birthDateParser);
  }

  @Test
  void shouldReturnExistingUserWhenAlreadyPresentInDatabase() {
    Jwt jwt =
        Jwt.withTokenValue("token")
            .header("alg", "none")
            .subject("external-auth-id")
            .claim("email", "test@test.com")
            .claim("preferred_username", "user1")
            .build();

    User existingUser = mock(User.class);

    when(userRepository.findByExternalAuthId("external-auth-id"))
        .thenReturn(Optional.of(existingUser));

    User result = handler.handle(jwt);

    assertEquals(existingUser, result);
    verify(userRepository, never()).save(any());
  }

  @Test
  void shouldCreateUserWhenNotFoundInDatabase() {
    Instant now = Instant.parse("2026-03-21T10:00:00Z");

    Jwt jwt =
        Jwt.withTokenValue("token")
            .header("alg", "none")
            .subject("external-auth-id")
            .claim("email", "sofia@example.com")
            .claim("preferred_username", "ksarlary1")
            .claim("given_name", "Sofia")
            .claim("family_name", "Konovalova")
            .claim("birthDate", "12.10.2000")
            .build();

    when(userRepository.findByExternalAuthId("external-auth-id")).thenReturn(Optional.empty());
    when(domainIdGenerator.generate()).thenReturn("user-id-123");
    when(timeProvider.now()).thenReturn(now);
    when(birthDateParser.parse("12.10.2000")).thenReturn(LocalDate.of(2000, 10, 12));

    User result = handler.handle(jwt);

    assertNotNull(result);
    verify(userRepository).save(any(User.class));
  }

  @Test
  void shouldThrowWhenBirthDateIsMissing() {
    Jwt jwt =
        Jwt.withTokenValue("token")
            .header("alg", "none")
            .subject("external-auth-id")
            .claim("email", "sofia@example.com")
            .claim("preferred_username", "ksarlary1")
            .claim("given_name", "Sofia")
            .claim("family_name", "Konovalova")
            .build();

    when(userRepository.findByExternalAuthId("external-auth-id")).thenReturn(Optional.empty());

    BusinessException exception = assertThrows(BusinessException.class, () -> handler.handle(jwt));

    assertEquals(BusinessErrorCodes.MISSING_BIRTHDATE, exception.getCode());
  }

  @Test
  void shouldThrowWhenUserIsUnder18() {
    Instant now = Instant.parse("2026-03-21T10:00:00Z");

    Jwt jwt =
        Jwt.withTokenValue("token")
            .header("alg", "none")
            .subject("external-auth-id")
            .claim("email", "sofia@example.com")
            .claim("preferred_username", "ksarlary1")
            .claim("given_name", "Sofia")
            .claim("family_name", "Konovalova")
            .claim("birthDate", "12.10.2010")
            .build();

    when(userRepository.findByExternalAuthId("external-auth-id")).thenReturn(Optional.empty());
    when(birthDateParser.parse("12.10.2010")).thenReturn(LocalDate.of(2010, 10, 12));
    when(timeProvider.now()).thenReturn(now);

    BusinessException exception = assertThrows(BusinessException.class, () -> handler.handle(jwt));

    assertEquals(BusinessErrorCodes.USER_TOO_YOUNG, exception.getCode());
    verify(userRepository, never()).save(any());
  }
}

package org.underdogs.users.application.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.underdogs.users.domain.UserId;
import org.underdogs.users.domain.UserStatus;

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
    Instant now = Instant.parse("2026-03-21T10:00:00Z");

    Jwt jwt =
        Jwt.withTokenValue("token")
            .header("alg", "none")
            .subject("external-auth-id")
            .claim("birthDate", "2000-10-12")
            .build();

    User existingUser =
        User.createFromIdentityProvider(
            new UserId("user-id"),
            "external-auth-id",
            "ksarlary",
            "test@example.com",
            "Sofia",
            "Konovalova",
            LocalDate.of(2000, 10, 12),
            now);

    when(userRepository.findByExternalAuthId("external-auth-id"))
        .thenReturn(Optional.of(existingUser));
    when(timeProvider.now()).thenReturn(now);
    when(birthDateParser.parse("2000-10-12")).thenReturn(LocalDate.of(2000, 10, 12));

    User result = handler.handle(jwt);

    assertEquals(existingUser, result);
    assertEquals(UserStatus.ACTIVE, result.getStatus());
    assertNull(result.getBlockedReason());

    verify(userRepository).save(existingUser);
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
    assertEquals(UserStatus.ACTIVE, result.getStatus());
    assertNull(result.getBlockedReason());

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
  void shouldBlockUserWhenUserIsUnder18() {
    Instant now = Instant.parse("2026-03-21T10:00:00Z");

    Jwt jwt =
        Jwt.withTokenValue("token")
            .header("alg", "none")
            .subject("external-auth-id")
            .claim("email", "young@example.com")
            .claim("preferred_username", "young_user")
            .claim("given_name", "Young")
            .claim("family_name", "User")
            .claim("birthDate", "2010-01-01")
            .build();

    when(userRepository.findByExternalAuthId("external-auth-id")).thenReturn(Optional.empty());
    when(domainIdGenerator.generate()).thenReturn("user-id");
    when(timeProvider.now()).thenReturn(now);
    when(birthDateParser.parse("2010-01-01")).thenReturn(LocalDate.of(2010, 1, 1));

    User user = handler.handle(jwt);

    assertEquals(UserStatus.BLOCKED, user.getStatus());
    assertEquals("You must be at least 18 years old to use Underdogs.", user.getBlockedReason());

    verify(userRepository).save(user);
  }
}

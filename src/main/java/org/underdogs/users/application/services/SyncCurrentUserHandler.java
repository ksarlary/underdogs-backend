package org.underdogs.users.application.services;

import java.time.LocalDate;
import java.time.ZoneOffset;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.underdogs.shared.BirthDateParser;
import org.underdogs.shared.DomainIdGenerator;
import org.underdogs.shared.TimeProvider;
import org.underdogs.shared.error.BusinessException;
import org.underdogs.users.application.gateways.UserRepository;
import org.underdogs.users.application.usecases.SyncCurrentUser;
import org.underdogs.users.domain.User;
import org.underdogs.users.domain.UserId;

@Service
class SyncCurrentUserHandler implements SyncCurrentUser {

  private final UserRepository userRepository;
  private final DomainIdGenerator domainIdGenerator;
  private final TimeProvider timeProvider;
  private final BirthDateParser birthDateParser;

  SyncCurrentUserHandler(
      UserRepository userRepository,
      DomainIdGenerator domainIdGenerator,
      TimeProvider timeProvider,
      BirthDateParser birthDateParser) {
    this.userRepository = userRepository;
    this.domainIdGenerator = domainIdGenerator;
    this.timeProvider = timeProvider;
    this.birthDateParser = birthDateParser;
  }

  @Override
  @Transactional
  public User handle(Jwt jwt) {
    final String externalAuthId = jwt.getSubject();

    return userRepository
        .findByExternalAuthId(externalAuthId)
        .orElseGet(() -> createUserFromJwt(jwt));
  }

  private User createUserFromJwt(Jwt jwt) {
    final String email = jwt.getClaimAsString("email");
    final String username = jwt.getClaimAsString("preferred_username");
    final String firstName = jwt.getClaimAsString("given_name");
    final String lastName = jwt.getClaimAsString("family_name");
    final String birthDateClaim = jwt.getClaimAsString("birthDate");

    if (email == null || email.isBlank()) {
      throw new BusinessException("MISSING_EMAIL", "Email is missing from the identity provider");
    }

    if (username == null || username.isBlank()) {
      throw new BusinessException(
          "MISSING_USERNAME", "Username is missing from the identity provider");
    }

    if (firstName == null || firstName.isBlank()) {
      throw new BusinessException(
          "MISSING_FIRST_NAME", "First name is missing from the identity provider");
    }

    if (lastName == null || lastName.isBlank()) {
      throw new BusinessException(
          "MISSING_LAST_NAME", "Last name is missing from the identity provider");
    }

    if (birthDateClaim == null || birthDateClaim.isBlank()) {
      throw new BusinessException(
          "MISSING_BIRTHDATE", "Birth date is missing from the identity provider");
    }

    final LocalDate birthDate = birthDateParser.parse(birthDateClaim);
    final LocalDate today = timeProvider.now().atZone(ZoneOffset.UTC).toLocalDate();

    if (birthDate.isAfter(today.minusYears(18))) {
      throw new BusinessException("USER_TOO_YOUNG", "You must be at least 18 years old");
    }

    final User user =
        User.createFromIdentityProvider(
            new UserId(domainIdGenerator.generate()),
            jwt.getSubject(),
            username,
            email,
            firstName,
            lastName,
            birthDate,
            timeProvider.now());

    userRepository.save(user);
    return user;
  }
}

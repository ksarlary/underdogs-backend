package org.underdogs.users.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.shared.error.BusinessException;
import org.underdogs.users.application.models.CreateUserRequest;

@Entity
@Table(name = "users")
@Access(AccessType.FIELD)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long technicalId;

  @Embedded private UserId id;

  @Column(nullable = false, unique = true)
  private String externalAuthId;

  @Column(nullable = false, unique = true, length = 50)
  private String username;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private LocalDate birthDate;

  @Column(nullable = false, length = 40)
  private String firstName;

  @Column(nullable = false, length = 40)
  private String lastName;

  @Column(nullable = false)
  private long kibblesBalance;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private UserRole role;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  protected User() {}

  private User(
      UserId id,
      String externalAuthId,
      String username,
      String email,
      String firstName,
      String lastName,
      LocalDate birthDate,
      long kibblesBalance,
      UserRole role,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.externalAuthId = externalAuthId;
    this.username = username;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.birthDate = birthDate;
    this.kibblesBalance = kibblesBalance;
    this.role = role;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static User create(UserId id, CreateUserRequest request, Instant now) {
    return new User(
        id,
        request.externalAuthId(),
        request.username(),
        request.email(),
        request.firstName(),
        request.lastName(),
        request.birthDate(),
        1000L,
        UserRole.USER,
        now,
        now);
  }

  public static User createFromIdentityProvider(
      UserId id,
      String externalAuthId,
      String username,
      String email,
      String firstName,
      String lastName,
      LocalDate birthDate,
      Instant now) {
    return new User(
        id,
        externalAuthId,
        username,
        email,
        firstName,
        lastName,
        birthDate,
        1000L,
        UserRole.USER,
        now,
        now);
  }

  public void updateProfile(String displayName, String email, Instant updatedAt) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.updatedAt = updatedAt;
  }

  public void creditKibbles(long amount) {
    if (amount <= 0) {
      throw new BusinessException(
          BusinessErrorCodes.INVALID_KIBBLES_AMOUNT, "Amount must be positive");
    }

    this.kibblesBalance += amount;
  }

  public void debitKibbles(long amount) {
    if (amount <= 0) {
      throw new BusinessException(
          BusinessErrorCodes.INVALID_KIBBLES_AMOUNT, "Amount must be positive");
    }

    if (kibblesBalance < amount) {
      throw new BusinessException(BusinessErrorCodes.INSUFFICIENT_KIBBLES, "Not enough kibbles");
    }

    kibblesBalance -= amount;
  }

  public Long getTechnicalId() {
    return technicalId;
  }

  public UserId getId() {
    return id;
  }

  public String getExternalAuthId() {
    return externalAuthId;
  }

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public long getKibblesBalance() {
    return kibblesBalance;
  }

  public UserRole getRole() {
    return role;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}

package org.underdogs.users.domain;

import org.underdogs.users.application.models.CreateUserRequest;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Access(AccessType.FIELD)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long technicalId;

    @Embedded
    private UserId id;

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
            Instant updatedAt
    ) {
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
                now
        );
    }

    public static User createFromIdentityProvider(
            UserId id,
            String externalAuthId,
            String username,
            String email,
            String firstName,
            String lastName,
            LocalDate birthDate,
            Instant now
    ) {
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
                now
        );
    }

    public void updateProfile(String displayName, String email, Instant updatedAt) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.updatedAt = updatedAt;
    }

    public void creditKibbles(long amount, Instant updatedAt) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        this.kibblesBalance += amount;
        this.updatedAt = updatedAt;
    }

    public void debitKibbles(long amount, Instant updatedAt) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (this.kibblesBalance < amount) {
            throw new IllegalStateException("Insufficient kibbles balance");
        }
        this.kibblesBalance -= amount;
        this.updatedAt = updatedAt;
    }

    public Long getTechnicalId() { return technicalId; }
    public UserId getId() { return id; }
    public String getExternalAuthId() { return externalAuthId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public long getKibblesBalance() { return kibblesBalance; }
    public UserRole getRole() { return role; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}

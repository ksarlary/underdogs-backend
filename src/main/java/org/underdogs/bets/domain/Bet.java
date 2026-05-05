package org.underdogs.bets.domain;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import org.underdogs.matches.domain.Match;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.shared.error.BusinessException;
import org.underdogs.teams.domain.Team;
import org.underdogs.users.domain.User;

@Entity
@Table(name = "bets")
@Access(AccessType.FIELD)
public class Bet {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long technicalId;

  @Embedded private BetId id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_technical_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "match_technical_id", nullable = false)
  private Match match;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "selected_team_technical_id", nullable = false)
  private Team selectedTeam;

  @Column(nullable = false)
  private long amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private BetStatus status;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column private Instant resolvedAt;

  @Column(nullable = false)
  private double coefficient;

  @Column(nullable = false)
  private long potentialGain;

  protected Bet() {}

  private Bet(
      BetId id,
      User user,
      Match match,
      Team selectedTeam,
      long amount,
      double coefficient,
      long potentialGain,
      BetStatus status,
      Instant createdAt,
      Instant resolvedAt) {
    this.id = id;
    this.user = user;
    this.match = match;
    this.selectedTeam = selectedTeam;
    this.amount = amount;
    this.coefficient = coefficient;
    this.potentialGain = potentialGain;
    this.status = status;
    this.createdAt = createdAt;
    this.resolvedAt = resolvedAt;
  }

  public static Bet create(
      BetId id,
      User user,
      Match match,
      Team selectedTeam,
      long amount,
      double coefficient,
      long potentialGain,
      Instant createdAt) {
    if (user == null) {
      throw new IllegalArgumentException("Bet user cannot be null");
    }

    if (match == null) {
      throw new IllegalArgumentException("Bet match cannot be null");
    }

    if (selectedTeam == null) {
      throw new IllegalArgumentException("Selected team cannot be null");
    }

    if (amount <= 0) {
      throw new BusinessException(
          BusinessErrorCodes.INVALID_BET_AMOUNT, "Bet amount must be positive");
    }

    if (coefficient < 1.0) {
      throw new BusinessException(
          BusinessErrorCodes.INVALID_BET_COEFFICIENT, "Bet coefficient must be at least 1.0");
    }

    if (potentialGain < amount) {
      throw new BusinessException(
          BusinessErrorCodes.INVALID_POTENTIAL_GAIN,
          "Potential gain cannot be lower than bet amount");
    }

    return new Bet(
        id,
        user,
        match,
        selectedTeam,
        amount,
        coefficient,
        potentialGain,
        BetStatus.PENDING,
        createdAt,
        null);
  }

  public void markWon(Instant resolvedAt) {
    if (status != BetStatus.PENDING) {
      throw new BusinessException(
          BusinessErrorCodes.BET_ALREADY_RESOLVED, "Bet has already been resolved");
    }

    this.status = BetStatus.WON;
    this.resolvedAt = resolvedAt;
  }

  public void markLost(Instant resolvedAt) {
    if (status != BetStatus.PENDING) {
      throw new BusinessException(
          BusinessErrorCodes.BET_ALREADY_RESOLVED, "Bet has already been resolved");
    }

    this.status = BetStatus.LOST;
    this.resolvedAt = resolvedAt;
  }

  public void cancel(Instant resolvedAt) {
    if (status != BetStatus.PENDING) {
      throw new BusinessException(
          BusinessErrorCodes.BET_ALREADY_RESOLVED, "Bet has already been resolved");
    }

    this.status = BetStatus.CANCELLED;
    this.resolvedAt = resolvedAt;
  }

  public Long getTechnicalId() {
    return technicalId;
  }

  public BetId getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public Match getMatch() {
    return match;
  }

  public Team getSelectedTeam() {
    return selectedTeam;
  }

  public long getAmount() {
    return amount;
  }

  public BetStatus getStatus() {
    return status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getResolvedAt() {
    return resolvedAt;
  }

  public double getCoefficient() {
    return coefficient;
  }

  public long getPotentialGain() {
    return potentialGain;
  }
}

package org.underdogs.bets.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record BetId(@Column(name = "id", nullable = false, unique = true) String value) {
  public BetId {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("BetId cannot be blank");
    }
  }
}

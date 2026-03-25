package org.underdogs.matches.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record MatchId(@Column(name = "id", nullable = false, unique = true) String value) {
  public MatchId {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("MatchId cannot be blank");
    }
  }
}

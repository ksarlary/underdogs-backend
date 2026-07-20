package org.underdogs.tournaments.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record TournamentId(@Column(name = "id", nullable = false, unique = true) String value) {
  public TournamentId {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("TournamentId cannot be blank");
    }
  }
}

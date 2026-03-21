package org.underdogs.players.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record PlayerId(
        @Column(name = "id", nullable = false, unique = true)
        String value
) {
    public PlayerId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PlayerId cannot be blank");
        }
    }
}

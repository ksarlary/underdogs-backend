package org.underdogs.teams.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record TeamId(
        @Column(name = "id", nullable = false, unique = true)
        String value
) {
    public TeamId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TeamId cannot be blank");
        }
    }
}
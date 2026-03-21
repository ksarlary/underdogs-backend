package org.underdogs.players.application.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreatePlayerRequest(
        @NotBlank
        @Size(min = 2, max = 50)
        String nickname,

        @NotBlank
        @Size(min = 2, max = 100)
        String fullName,

        @Size(max = 50)
        String role,

        @NotBlank
        @Pattern(regexp = "^[A-Z]{2}$", message = "countryCode must be a 2-letter uppercase ISO code")
        String countryCode,

        @NotBlank
        String teamId
) {
}

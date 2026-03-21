package org.underdogs.users.application.models;

import jakarta.validation.constraints.Min;

public record CreditKibblesRequest(
        @Min(1)
        long amount
) {}

package org.underdogs.shared.error;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        String code,
        String message,
        Instant timestamp,
        List<String> details
) {
}

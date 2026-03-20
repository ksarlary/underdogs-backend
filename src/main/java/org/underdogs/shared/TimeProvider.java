package org.underdogs.shared;

import java.time.Instant;

public interface TimeProvider {
    Instant now();
}

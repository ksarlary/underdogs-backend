package org.underdogs.users.application.usecases;

import org.underdogs.users.domain.UserId;

public interface CreditKibbles {
    void handle(UserId userId, long amount);
}

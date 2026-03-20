package org.underdogs.users.application.usecases;

import org.underdogs.users.domain.User;
import org.underdogs.users.domain.UserId;

import java.util.Optional;

public interface SearchUserById {
    Optional<User> handle(UserId userId);
}
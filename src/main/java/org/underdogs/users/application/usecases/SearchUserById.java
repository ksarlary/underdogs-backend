package org.underdogs.users.application.usecases;

import java.util.Optional;
import org.underdogs.users.domain.User;
import org.underdogs.users.domain.UserId;

public interface SearchUserById {
  Optional<User> handle(UserId userId);
}

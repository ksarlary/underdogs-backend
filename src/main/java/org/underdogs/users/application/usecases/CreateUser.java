package org.underdogs.users.application.usecases;

import org.underdogs.users.application.models.CreateUserRequest;
import org.underdogs.users.domain.UserId;

public interface CreateUser {
  UserId handle(CreateUserRequest request);
}

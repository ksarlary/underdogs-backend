package org.underdogs.users.application.usecases;

import org.underdogs.users.application.models.UpdateUserRequest;
import org.underdogs.users.domain.UserId;

public interface UpdateUser {
  void handle(UserId userId, UpdateUserRequest request);
}

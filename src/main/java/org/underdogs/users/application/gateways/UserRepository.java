package org.underdogs.users.application.gateways;

import java.util.List;
import java.util.Optional;
import org.underdogs.users.domain.User;
import org.underdogs.users.domain.UserId;

public interface UserRepository {
  void save(User user);

  Optional<User> findById(UserId id);

  Optional<User> findByExternalAuthId(String externalAuthId);

  List<User> findAll();
}

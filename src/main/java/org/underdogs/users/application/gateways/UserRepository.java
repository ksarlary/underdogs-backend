package org.underdogs.users.application.gateways;

import org.underdogs.users.domain.User;
import org.underdogs.users.domain.UserId;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    void save(User user);
    Optional<User> findById(UserId id);
    Optional<User> findByExternalAuthId(String externalAuthId);
    List<User> findAll();
}

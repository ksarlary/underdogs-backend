package org.underdogs.users.application.services;

import java.util.List;
import org.springframework.stereotype.Service;
import org.underdogs.users.application.gateways.UserRepository;
import org.underdogs.users.application.usecases.SearchUsers;
import org.underdogs.users.domain.User;

@Service
class SearchUsersHandler implements SearchUsers {

  private final UserRepository userRepository;

  SearchUsersHandler(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public List<User> handle() {
    return userRepository.findAll();
  }
}

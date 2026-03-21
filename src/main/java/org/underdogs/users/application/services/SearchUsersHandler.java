package org.underdogs.users.application.services;

import org.underdogs.users.application.gateways.UserRepository;
import org.underdogs.users.application.usecases.SearchUsers;
import org.underdogs.users.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;

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

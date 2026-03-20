package org.underdogs.users.application.services;

import org.underdogs.users.application.gateways.UserRepository;
import org.underdogs.users.application.usecases.SearchUserById;
import org.underdogs.users.domain.User;
import org.underdogs.users.domain.UserId;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class SearchUserByIdHandler implements SearchUserById {

    private final UserRepository userRepository;

    SearchUserByIdHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> handle(UserId userId) {
        return userRepository.findById(userId);
    }
}

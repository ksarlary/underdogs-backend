package org.underdogs.users.application.services;

import org.underdogs.shared.TimeProvider;
import org.underdogs.shared.error.BusinessException;
import org.underdogs.users.application.gateways.UserRepository;
import org.underdogs.users.application.models.UpdateUserRequest;
import org.underdogs.users.application.usecases.UpdateUser;
import org.underdogs.users.domain.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class UpdateUserHandler implements UpdateUser {

    private final UserRepository userRepository;
    private final TimeProvider timeProvider;

    UpdateUserHandler(UserRepository userRepository, TimeProvider timeProvider) {
        this.userRepository = userRepository;
        this.timeProvider = timeProvider;
    }

    @Override
    @Transactional
    public void handle(UserId userId, UpdateUserRequest request) {
        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found"));

        user.updateProfile(request.displayName(), request.email(), timeProvider.now());
        userRepository.save(user);
    }
}

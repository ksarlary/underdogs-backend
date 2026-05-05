package org.underdogs.users.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.underdogs.shared.TimeProvider;
import org.underdogs.shared.error.BusinessException;
import org.underdogs.users.application.gateways.UserRepository;
import org.underdogs.users.application.usecases.CreditKibbles;
import org.underdogs.users.domain.UserId;

@Service
class CreditKibblesHandler implements CreditKibbles {

  private final UserRepository userRepository;
  private final TimeProvider timeProvider;

  CreditKibblesHandler(UserRepository userRepository, TimeProvider timeProvider) {
    this.userRepository = userRepository;
    this.timeProvider = timeProvider;
  }

  @Override
  @Transactional
  public void handle(UserId userId, long amount) {
    final var user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found"));

    user.creditKibbles(amount);
    userRepository.save(user);
  }
}

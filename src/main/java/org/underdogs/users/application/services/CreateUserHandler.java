package org.underdogs.users.application.services;

import org.underdogs.shared.DomainIdGenerator;
import org.underdogs.shared.TimeProvider;
import org.underdogs.users.application.gateways.UserRepository;
import org.underdogs.users.application.models.CreateUserRequest;
import org.underdogs.users.application.usecases.CreateUser;
import org.underdogs.users.domain.User;
import org.underdogs.users.domain.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class CreateUserHandler implements CreateUser {
        private static final Logger logger = LoggerFactory.getLogger(CreateUserHandler.class);

        private final DomainIdGenerator idGenerator;
        private final TimeProvider timeProvider;
        private final UserRepository userRepository;

        CreateUserHandler(
                DomainIdGenerator idGenerator,
                TimeProvider timeProvider,
                UserRepository userRepository
        ) {
                this.idGenerator = idGenerator;
                this.timeProvider = timeProvider;
                this.userRepository = userRepository;
        }

        @Override
        @Transactional
        public UserId handle(CreateUserRequest request) {
                logger.info("Creating user: username={}, email={}", request.username(), request.email());

                final var userId = new UserId(idGenerator.generate());
                final var user = User.create(userId, request, timeProvider.now());

                userRepository.save(user);

                logger.debug("User created successfully: id={}", userId.value());
                return userId;
        }
}

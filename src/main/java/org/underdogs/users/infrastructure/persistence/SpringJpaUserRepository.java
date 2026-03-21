package org.underdogs.users.infrastructure.persistence;

import org.underdogs.users.domain.User;
import org.underdogs.users.domain.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface SpringJpaUserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(UserId id);
    Optional<User> findByExternalAuthId(String externalAuthId);
}

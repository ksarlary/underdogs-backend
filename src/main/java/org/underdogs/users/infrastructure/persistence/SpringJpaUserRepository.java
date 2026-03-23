package org.underdogs.users.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.underdogs.users.domain.User;
import org.underdogs.users.domain.UserId;

interface SpringJpaUserRepository extends JpaRepository<User, Long> {
  Optional<User> findById(UserId id);

  Optional<User> findByExternalAuthId(String externalAuthId);
}

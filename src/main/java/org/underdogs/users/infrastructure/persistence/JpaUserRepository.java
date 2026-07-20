package org.underdogs.users.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.underdogs.users.application.gateways.UserRepository;
import org.underdogs.users.domain.User;
import org.underdogs.users.domain.UserId;

@Repository
class JpaUserRepository implements UserRepository {

  private final SpringJpaUserRepository jpaRepository;

  JpaUserRepository(SpringJpaUserRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public void save(User user) {
    jpaRepository.save(user);
  }

  @Override
  public Optional<User> findById(UserId id) {
    return jpaRepository.findById(id);
  }

  @Override
  public Optional<User> findByExternalAuthId(String externalAuthId) {
    return jpaRepository.findByExternalAuthId(externalAuthId);
  }

  @Override
  public List<User> findAll() {
    return jpaRepository.findAll();
  }
}

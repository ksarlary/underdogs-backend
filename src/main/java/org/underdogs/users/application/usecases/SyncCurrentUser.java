package org.underdogs.users.application.usecases;

import org.springframework.security.oauth2.jwt.Jwt;
import org.underdogs.users.domain.User;

public interface SyncCurrentUser {
  User handle(Jwt jwt);
}

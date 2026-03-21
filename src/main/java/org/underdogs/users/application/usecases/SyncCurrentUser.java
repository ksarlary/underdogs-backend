package org.underdogs.users.application.usecases;

import org.underdogs.users.domain.User;
import org.springframework.security.oauth2.jwt.Jwt;

public interface SyncCurrentUser {
    User handle(Jwt jwt);
}

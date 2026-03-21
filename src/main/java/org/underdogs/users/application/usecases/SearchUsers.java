package org.underdogs.users.application.usecases;

import org.underdogs.users.domain.User;

import java.util.List;

public interface SearchUsers {
    List<User> handle();
}

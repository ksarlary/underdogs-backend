package org.underdogs.users.application.usecases;

import java.util.List;
import org.underdogs.users.domain.User;

public interface SearchUsers {
  List<User> handle();
}

package org.underdogs.users.infrastructure.rest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.underdogs.users.application.usecases.SyncCurrentUser;
import org.underdogs.users.infrastructure.rest.dto.UserDetailDTO;
import org.underdogs.users.infrastructure.rest.mapper.UserMapper;

@RestController
@RequestMapping("/api/v1/users")
public class CurrentUserController {

  private final SyncCurrentUser syncCurrentUser;
  private final UserMapper userMapper;

  public CurrentUserController(SyncCurrentUser syncCurrentUser, UserMapper userMapper) {
    this.syncCurrentUser = syncCurrentUser;
    this.userMapper = userMapper;
  }

  @GetMapping("/me")
  public UserDetailDTO me(@AuthenticationPrincipal Jwt jwt) {
    return userMapper.toDetailDTO(syncCurrentUser.handle(jwt));
  }
}

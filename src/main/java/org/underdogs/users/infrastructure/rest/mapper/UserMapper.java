package org.underdogs.users.infrastructure.rest.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import org.underdogs.users.domain.User;
import org.underdogs.users.infrastructure.rest.dto.UserDetailDTO;
import org.underdogs.users.infrastructure.rest.dto.UserSummaryDTO;

@Component
public class UserMapper {

  public UserSummaryDTO toSummaryDTO(User user) {
    return new UserSummaryDTO(
        user.getId().value(),
        user.getUsername(),
        user.getKibblesBalance(),
        user.getRole().name(),
        user.getCreatedAt());
  }

  public UserDetailDTO toDetailDTO(User user) {
    return new UserDetailDTO(
        user.getId().value(),
        user.getExternalAuthId(),
        user.getUsername(),
        user.getEmail(),
        user.getFirstName(),
        user.getLastName(),
        user.getBirthDate(),
        user.getKibblesBalance(),
        user.getRole().name(),
        user.getCreatedAt(),
        user.getUpdatedAt());
  }

  public List<UserSummaryDTO> toSummaryDTOList(List<User> users) {
    return users.stream().map(this::toSummaryDTO).toList();
  }
}

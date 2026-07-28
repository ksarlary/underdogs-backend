package org.underdogs.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.shared.error.ErrorResponse;
import org.underdogs.users.application.gateways.UserRepository;

@Component
public class BlockedUserFilter extends OncePerRequestFilter {

  private static final List<String> ALLOWED_PREFIXES =
      List.of(
          "/api/v1/users/me",
          "/api/v1/matches",
          "/api/v2/matches",
          "/api/v1/teams",
          "/api/v1/players",
          "/api/v1/tournaments",
          "/v3/api-docs",
          "/swagger-ui");

  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;

  public BlockedUserFilter(
      @Lazy UserRepository userRepository, @Qualifier("objectMapper") ObjectMapper objectMapper) {
    this.userRepository = userRepository;
    this.objectMapper = objectMapper;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();

    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      return true;
    }

    return ALLOWED_PREFIXES.stream().anyMatch(path::startsWith);
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
      filterChain.doFilter(request, response);
      return;
    }

    Jwt jwt = jwtAuthenticationToken.getToken();
    String externalAuthId = jwt.getSubject();

    var user = userRepository.findByExternalAuthId(externalAuthId);

    if (user.isPresent() && user.get().isBlocked()) {
      writeBlockedResponse(response, user.get().getBlockedReason());
      return;
    }

    filterChain.doFilter(request, response);
  }

  @Override
  protected void doFilterNestedErrorDispatch(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    filterChain.doFilter(request, response);
  }

  private void writeBlockedResponse(HttpServletResponse response, String reason)
      throws IOException {
    response.setStatus(HttpStatus.FORBIDDEN.value());
    response.setContentType("application/json");

    ErrorResponse errorResponse =
        new ErrorResponse(BusinessErrorCodes.USER_BLOCKED, reason, Instant.now(), List.of());

    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }

  private static class BlockedUserSecurityException extends RuntimeException {
    private BlockedUserSecurityException(String message) {
      super(message);
    }
  }
}

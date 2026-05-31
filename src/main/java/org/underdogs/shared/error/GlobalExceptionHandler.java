package org.underdogs.shared.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.underdogs.shared.TimeProvider;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  private final TimeProvider timeProvider;

  public GlobalExceptionHandler(TimeProvider timeProvider) {
    this.timeProvider = timeProvider;
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(
      BusinessException exception, HttpServletRequest request) {
    logger.warn(
        "Business exception on {} {} - code={}, message={}",
        request.getMethod(),
        request.getRequestURI(),
        exception.getCode(),
        exception.getMessage());

    ErrorResponse response =
        new ErrorResponse(
            exception.getCode(), exception.getMessage(), timeProvider.now(), List.of());

    return ResponseEntity.status(mapBusinessCodeToStatus(exception.getCode())).body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException exception, HttpServletRequest request) {
    logger.warn("Validation error on {} {}", request.getMethod(), request.getRequestURI());

    List<String> details =
        exception.getBindingResult().getAllErrors().stream()
            .map(
                error -> {
                  if (error instanceof FieldError fieldError) {
                    return fieldError.getField() + " " + fieldError.getDefaultMessage();
                  }
                  return error.getDefaultMessage();
                })
            .toList();

    ErrorResponse response =
        new ErrorResponse(
            "VALIDATION_ERROR", "Request validation failed", timeProvider.now(), details);

    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(
      ConstraintViolationException exception, HttpServletRequest request) {
    logger.warn("Constraint violation on {} {}", request.getMethod(), request.getRequestURI());

    List<String> details =
        exception.getConstraintViolations().stream()
            .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
            .toList();

    ErrorResponse response =
        new ErrorResponse("VALIDATION_ERROR", "Constraint violation", timeProvider.now(), details);

    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException exception, HttpServletRequest request) {
    logger.warn(
        "Illegal argument on {} {} - {}",
        request.getMethod(),
        request.getRequestURI(),
        exception.getMessage());

    ErrorResponse response =
        new ErrorResponse("BAD_REQUEST", exception.getMessage(), timeProvider.now(), List.of());

    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(
      AccessDeniedException exception, HttpServletRequest request) {
    logger.warn(
        "Access denied on {} {} - {}",
        request.getMethod(),
        request.getRequestURI(),
        exception.getMessage());

    ErrorResponse response =
        new ErrorResponse(
            "FORBIDDEN",
            "You do not have permission to access this resource",
            timeProvider.now(),
            List.of());

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleAuthenticationException(
      AuthenticationException exception, HttpServletRequest request) {
    logger.warn(
        "Authentication failed on {} {} - {}",
        request.getMethod(),
        request.getRequestURI(),
        exception.getMessage());

    ErrorResponse response =
        new ErrorResponse(
            "UNAUTHORIZED",
            "Authentication is required to access this resource",
            timeProvider.now(),
            List.of());

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnexpectedException(
      Exception exception, HttpServletRequest request) {
    logger.error(
        "Unexpected error on {} {}", request.getMethod(), request.getRequestURI(), exception);

    ErrorResponse response =
        new ErrorResponse(
            "INTERNAL_SERVER_ERROR", "An unexpected error occurred", timeProvider.now(), List.of());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }

  private HttpStatus mapBusinessCodeToStatus(String code) {
    return switch (code) {
      case BusinessErrorCodes.USER_NOT_FOUND,
              BusinessErrorCodes.TEAM_NOT_FOUND,
              BusinessErrorCodes.TOURNAMENT_NOT_FOUND,
              BusinessErrorCodes.MATCH_NOT_FOUND,
              BusinessErrorCodes.PLAYER_NOT_FOUND,
              BusinessErrorCodes.BET_NOT_FOUND ->
          HttpStatus.NOT_FOUND;

      case BusinessErrorCodes.USER_ALREADY_EXISTS,
              BusinessErrorCodes.TEAM_NAME_ALREADY_EXISTS,
              BusinessErrorCodes.TEAM_TAG_ALREADY_EXISTS,
              BusinessErrorCodes.PLAYER_ALREADY_EXISTS,
              BusinessErrorCodes.TOURNAMENT_ALREADY_EXISTS,
              BusinessErrorCodes.INSUFFICIENT_KIBBLES,
              BusinessErrorCodes.BET_ALREADY_EXISTS,
              BusinessErrorCodes.BET_ALREADY_RESOLVED ->
          HttpStatus.CONFLICT;

      case BusinessErrorCodes.MISSING_EMAIL,
              BusinessErrorCodes.MISSING_USERNAME,
              BusinessErrorCodes.MISSING_FIRST_NAME,
              BusinessErrorCodes.MISSING_LAST_NAME,
              BusinessErrorCodes.MISSING_BIRTHDATE,
              BusinessErrorCodes.INVALID_BIRTHDATE_FORMAT,
              BusinessErrorCodes.INVALID_MATCH_TEAMS,
              BusinessErrorCodes.INVALID_MATCH_WINNER,
              BusinessErrorCodes.USER_TOO_YOUNG,
              BusinessErrorCodes.INVALID_KIBBLES_AMOUNT,
              BusinessErrorCodes.INVALID_BET_AMOUNT,
              BusinessErrorCodes.MATCH_NOT_OPEN_FOR_BETS,
              BusinessErrorCodes.TEAM_NOT_IN_MATCH,
              BusinessErrorCodes.INVALID_BET_COEFFICIENT,
              BusinessErrorCodes.INVALID_POTENTIAL_GAIN,
              BusinessErrorCodes.MATCH_RESULT_REQUIRED,
              BusinessErrorCodes.MATCH_DRAW_NOT_ALLOWED,
              BusinessErrorCodes.INVALID_MATCH_STATUS_TRANSITION,
              BusinessErrorCodes.MATCH_WINNER_REQUIRED ->
          HttpStatus.BAD_REQUEST;

      case BusinessErrorCodes.USER_BLOCKED -> HttpStatus.FORBIDDEN;

      default -> HttpStatus.BAD_REQUEST;
    };
  }
}

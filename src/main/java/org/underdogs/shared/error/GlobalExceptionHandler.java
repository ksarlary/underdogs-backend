package org.underdogs.shared.error;

import org.underdogs.shared.TimeProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final TimeProvider timeProvider;

    public GlobalExceptionHandler(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException exception,
            HttpServletRequest request
    ) {
        logger.warn("Business exception on {} {} - code={}, message={}",
                request.getMethod(),
                request.getRequestURI(),
                exception.getCode(),
                exception.getMessage()
        );

        ErrorResponse response = new ErrorResponse(
                exception.getCode(),
                exception.getMessage(),
                timeProvider.now(),
                List.of()
        );

        return ResponseEntity.status(mapBusinessCodeToStatus(exception.getCode())).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        logger.warn("Validation error on {} {}",
                request.getMethod(),
                request.getRequestURI()
        );

        List<String> details = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return fieldError.getField() + " " + fieldError.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .toList();

        ErrorResponse response = new ErrorResponse(
                "VALIDATION_ERROR",
                "Request validation failed",
                timeProvider.now(),
                details
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        logger.warn("Constraint violation on {} {}",
                request.getMethod(),
                request.getRequestURI()
        );

        List<String> details = exception.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                .toList();

        ErrorResponse response = new ErrorResponse(
                "VALIDATION_ERROR",
                "Constraint violation",
                timeProvider.now(),
                details
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        logger.warn("Illegal argument on {} {} - {}",
                request.getMethod(),
                request.getRequestURI(),
                exception.getMessage()
        );

        ErrorResponse response = new ErrorResponse(
                "BAD_REQUEST",
                exception.getMessage(),
                timeProvider.now(),
                List.of()
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
    ) {
        logger.error("Unexpected error on {} {}",
                request.getMethod(),
                request.getRequestURI(),
                exception
        );

        ErrorResponse response = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                timeProvider.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private HttpStatus mapBusinessCodeToStatus(String code) {
        return switch (code) {
            case "USER_NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "USER_ALREADY_EXISTS" -> HttpStatus.CONFLICT;
            case "INSUFFICIENT_KIBBLES" -> HttpStatus.CONFLICT;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}

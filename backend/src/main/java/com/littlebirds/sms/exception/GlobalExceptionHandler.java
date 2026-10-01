package com.littlebirds.sms.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.littlebirds.sms.dto.ApiError;

/**
 * Turns every error into the same JSON shape (ApiError) with a meaningful status code and no stack trace.
 *
 * Extending ResponseEntityExceptionHandler also covers Spring's own errors (bad JSON, wrong method,
 * missing parameter, unknown URL) so they use the same shape.
 *
 * Spring Security's AccessDeniedException (a role without the permission) must be handled here, otherwise the
 * catch-all below would turn it into a 500.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ---------------------------------------------------------------- our own exceptions

    @ExceptionHandler({InvalidRequestException.class, InvalidMarkException.class})
    public ResponseEntity<ApiError> handleBadRequest(RuntimeException ex) {
        return respond(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler({StudentNotFoundException.class, TeacherNotFoundException.class,
            StaffNotFoundException.class, MarksNotFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(RuntimeException ex) {
        return respond(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({StudentAlreadyExistsException.class, TeacherAlreadyExistsException.class,
            StaffAlreadyExistsException.class})
    public ResponseEntity<ApiError> handleConflict(RuntimeException ex) {
        return respond(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ApiError> handleForbidden(ForbiddenOperationException ex) {
        return respond(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    /** @PreAuthorize said no: the logged-in role does not have this permission. */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
        return respond(HttpStatus.FORBIDDEN, "Access Denied..!! You do not have permission to do this.");
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(InvalidCredentialsException ex) {
        return respond(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication(AuthenticationException ex) {
        return respond(HttpStatus.UNAUTHORIZED, "Authentication is required. Please log in.");
    }

    /** A database rule was hit anyway (for example two people creating the same ID at the same moment). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return respond(HttpStatus.CONFLICT,
                "The request conflicts with existing data (for example a duplicate ID or username).");
    }

    /** Anything unexpected: log the details on the server, tell the client nothing internal. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return respond(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong. Please try again later.");
    }

    // ---------------------------------------------------------------- Spring MVC errors

    /** Bean Validation failures: one message per field. */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(cleanFieldName(error.getField()), error.getDefaultMessage());
        }
        return ResponseEntity.status(status).body(ApiError.of(status.value(), "Validation failed", errors));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(status).body(ApiError.of(status.value(),
                "Request body is missing or malformed. Dates must use the format yyyy-MM-dd."));
    }

    /** Everything else Spring raises (405, 404, 415, missing parameter, bad parameter type ...). */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        if (status.is5xxServerError()) {
            log.error("Request failed", ex);
        }
        return ResponseEntity.status(status).headers(headers)
                .body(ApiError.of(status.value(), messageFor(ex, status)));
    }

    // ---------------------------------------------------------------- helpers

    private static ResponseEntity<ApiError> respond(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ApiError.of(status.value(), message));
    }

    /** Messages that are safe to show; anything technical is replaced by a plain sentence. */
    private static String messageFor(Exception ex, HttpStatusCode status) {
        if (ex instanceof ServletRequestBindingException || ex instanceof MissingServletRequestParameterException) {
            return ex.getMessage(); // e.g. "Required request parameter 'subject' ... is not present"
        }
        if (ex instanceof TypeMismatchException mismatch) {
            return "Invalid value for '" + mismatch.getPropertyName() + "'";
        }
        return switch (status.value()) {
            case 400 -> "Bad request";
            case 404 -> "The requested resource was not found.";
            case 405 -> "This HTTP method is not allowed for this URL.";
            case 415 -> "Unsupported content type. Use application/json.";
            default -> status.is5xxServerError() ? "Something went wrong. Please try again later." : "Request failed";
        };
    }

    /** "marks[English].<map value>" becomes "marks[English]". */
    private static String cleanFieldName(String field) {
        return field.replaceAll("\\.?<[^>]*>", "");
    }
}

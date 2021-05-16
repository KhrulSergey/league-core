package com.freetonleague.core.exception.config;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.freetonleague.core.exception.*;
import com.freetonleague.core.exception.model.ApiError;
import com.freetonleague.core.exception.model.ValidationError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Value("${debug-rest:false}")
    private boolean enableDebugRest;

    @Value("${debug:false}")
    private boolean enableDebugApp;

    @Value("${stacktrace-rest:false}")
    private boolean enableStackTrace;

    private boolean enableDebugMessage() {
        return enableDebugRest || enableDebugApp;
    }

    //<editor-fold desc="Validation -> HttpStatus.BAD_REQUEST">
    @ExceptionHandler({ValidationException.class})
    protected ResponseEntity<Object> handleEntityNotFoundEx(ValidationException ex, WebRequest request) {

        String debugMessage = enableDebugMessage() ? ex.getDetailedMessage() : null;
        List<String> errors = enableStackTrace ? ex.getValidationErrors().stream()
                .map(ValidationError::toString).collect(Collectors.toList()) : null;
        ApiError apiError = new ApiError(ex.getMessage(), debugMessage, errors);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                                      WebRequest request) {
        ApiError apiError = new ApiError();
        apiError.setMessage(String.format("The parameter '%s' of value '%s' could not be converted to type '%s'",
                ex.getName(), ex.getValue(), Objects.requireNonNull(ex.getRequiredType()).getSimpleName()));
        String debugMessage = enableDebugMessage() ? ex.getMessage() : null;
        apiError.setDebugMessage(debugMessage);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        List<String> errors = enableStackTrace ? ex.getConstraintViolations()
                .stream()
                .map(x -> x.getRootBeanClass().getName() + " " +
                        x.getPropertyPath() + ": " + x.getMessage())
                .collect(Collectors.toList())
                : null;
        String debugMessage = enableDebugMessage() ? ex.getMessage() : null;
        String message = String.format("Method arguments not valid, at least '%s'",
                ex.getConstraintViolations().iterator().next().getPropertyPath());
        ApiError apiError = new ApiError(message, debugMessage, errors);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        String debugMessage = String.format("Entity '%s' not found", ex.getMessage());
        ApiError apiError = new ApiError(ExceptionMessages.ENTITY_NOT_FOUND_ERROR, debugMessage);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @NotNull
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        String invalidParameter = "Unknown. Look stack trace";
        if (ex.getCause() instanceof InvalidFormatException) {
            invalidParameter = ((InvalidFormatException) ex.getCause()).getValue().toString();
        }
        String debugMessage = enableDebugMessage() ?
                String.format("Unacceptable arguments, at least '%s'", invalidParameter) : null;
        List<String> errors = enableStackTrace ? List.of(Objects.requireNonNull(ex.getMessage())) : null;
        ApiError apiError = new ApiError(ExceptionMessages.REQUEST_MESSAGE_READABLE_ERROR, debugMessage, errors);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @NotNull
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        String debugMessage = enableDebugMessage() ? ex.getMessage() : null;
        List<String> errors = enableStackTrace ? ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList())
                : null;

        ApiError apiError = new ApiError(ExceptionMessages.METHOD_ARGUMENT_VALIDATION_ERROR, debugMessage, errors);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex,
                                                                    WebRequest request) {
        String debugMessage = enableDebugMessage() ? ex.getMessage() : null;
        List<String> errors = enableStackTrace && nonNull(ex.getCause()) ? Collections.singletonList(ex.getCause().toString()) : null;
        ApiError apiError = new ApiError(ExceptionMessages.METHOD_ARGUMENT_VALIDATION_ERROR, debugMessage, errors);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
    //</editor-fold>

    //<editor-fold desc="Custom Exceptions -> HttpStatus.?Custom">
    @ExceptionHandler({BlockChainDuplicateException.class})
    public ResponseEntity<Object> handleBlockChainException(BlockChainDuplicateException ex, WebRequest request) {
        String debugMessage = enableDebugMessage() && nonNull(ex.getCause()) ? ex.getCause().toString() : null;
        ApiError apiError = new ApiError(ex.getMessage(), debugMessage);
        return new ResponseEntity<>(apiError, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler({ForbiddenException.class})
    public ResponseEntity<Object> handleForbiddenException(ForbiddenException ex, WebRequest request) {
        String debugMessage = enableDebugMessage() && nonNull(ex.getCause()) ? ex.getCause().toString() : null;
        ApiError apiError = new ApiError(ex.getMessage(), debugMessage);
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({CustomUnexpectedException.class})
    public ResponseEntity<Object> handleCustomUnexpectedException(CustomUnexpectedException ex, WebRequest request) {
        String debugMessage = enableDebugMessage() && nonNull(ex.getCause()) ? ex.getCause().toString() : null;
        ApiError apiError = new ApiError(ex.getMessage(), debugMessage);
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({UnauthorizedException.class})
    public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        String debugMessage = enableDebugMessage() ? ex.getDetailedMessage() : null;
        List<String> errors = enableStackTrace && nonNull(ex.getCause()) ? Collections.singletonList(ex.getCause().toString()) : null;
        ApiError apiError = new ApiError(ex.getMessage(), debugMessage, errors);
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({AuthProviderConfigException.class, UserManageException.class,
            TeamManageException.class, TeamParticipantManageException.class, GameDisciplineManageException.class,
            GameDisciplineSettingsManageException.class, TournamentManageException.class, FinancialUnitManageException.class,
            AccountFinanceManageException.class, DocketManageException.class})
    public ResponseEntity<Object> handleBaseDetailedException(BaseDetailedException ex, WebRequest request) {
        String debugMessage = enableDebugMessage() ? ex.getDetailedMessage() : null;
        List<String> errors = enableStackTrace && nonNull(ex.getCause()) ? Collections.singletonList(ex.getCause().toString()) : null;
        ApiError apiError = new ApiError(ex.getMessage(), debugMessage, errors);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> conflict(HttpServletRequest req, DataIntegrityViolationException ex) {
        String debugMessage = enableDebugMessage() ? ex.getMessage() : null;
        List<String> errors = enableStackTrace && nonNull(ex.getCause()) ? Collections.singletonList(ex.getCause().toString()) : null;

        ApiError apiError = new ApiError(ExceptionMessages.METHOD_ARGUMENT_VALIDATION_ERROR, debugMessage, errors);
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }
    //</editor-fold>
}

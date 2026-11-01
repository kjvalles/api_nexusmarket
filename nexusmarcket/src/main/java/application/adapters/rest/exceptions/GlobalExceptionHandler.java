package application.adapters.rest.exceptions;

import application.adapters.rest.dtos.responses.ErrorResponseDto;
import application.domain.exceptions.DomainException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidCredentialsException;
import application.domain.exceptions.InvalidOrderStateException;
import application.domain.exceptions.InvalidUserStatusException;
import application.domain.exceptions.NegativeStockException;
import application.domain.exceptions.ProductNotAvailableException;
import application.domain.exceptions.UnauthorizedRoleOperationException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manejador global centralizado de excepciones para la API REST de NexusMarket.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidCredentials(
            InvalidCredentialsException ex, HttpServletRequest request) {
        String requestId = resolveRequestId(request);
        log.warn("Autenticación fallida [requestId={}] {} {} -> {}",
                requestId, request.getMethod(), request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, "Credenciales inválidas", ex.getMessage(), request, requestId, null);
    }

    @ExceptionHandler({EntityNotFoundException.class, ProductNotAvailableException.class})
    public ResponseEntity<ErrorResponseDto> handleNotFound(
            RuntimeException ex, HttpServletRequest request) {
        String requestId = resolveRequestId(request);
        log.warn("Recurso no encontrado [requestId={}] {} {} -> {}: {}",
                requestId, request.getMethod(), request.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "Recurso no encontrado", ex.getMessage(), request, requestId, null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNoResourceFound(
            NoResourceFoundException ex, HttpServletRequest request) {
        String requestId = resolveRequestId(request);
        log.warn("Ruta no existente [requestId={}] {} {} -> {}",
                requestId, request.getMethod(), request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "Ruta no encontrada", ex.getMessage(), request, requestId, null);
    }

    @ExceptionHandler({UnauthorizedRoleOperationException.class, AccessDeniedException.class})
    public ResponseEntity<ErrorResponseDto> handleAccessDenied(
            Exception ex, HttpServletRequest request) {
        String requestId = resolveRequestId(request);
        log.warn("Acceso denegado o rol no autorizado [requestId={}] {} {} -> {}: {}",
                requestId, request.getMethod(), request.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, "Acceso denegado", ex.getMessage(), request, requestId, null);
    }

    @ExceptionHandler({NegativeStockException.class, InvalidOrderStateException.class, InvalidUserStatusException.class})
    public ResponseEntity<ErrorResponseDto> handleBusinessConflict(
            DomainException ex, HttpServletRequest request) {
        String requestId = resolveRequestId(request);
        log.warn("Conflicto de negocio (409) [requestId={}] {} {} -> {}: {}",
                requestId, request.getMethod(), request.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, "Conflicto de regla de negocio", ex.getMessage(), request, requestId, null);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponseDto> handleGenericDomain(
            DomainException ex, HttpServletRequest request) {
        String requestId = resolveRequestId(request);
        log.warn("Violación de regla de dominio [requestId={}] {} {} -> {}: {}",
                requestId, request.getMethod(), request.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, "Regla de dominio violada", ex.getMessage(), request, requestId, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String requestId = resolveRequestId(request);
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        log.warn("Fallo de validación de argumentos [requestId={}] {} {} -> {}",
                requestId, request.getMethod(), request.getRequestURI(), errors);
        return buildResponse(HttpStatus.BAD_REQUEST, "Error de validación", "Uno o más campos contienen errores.", request, requestId, errors);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, HandlerMethodValidationException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponseDto> handleBadRequest(
            Exception ex, HttpServletRequest request) {
        String requestId = resolveRequestId(request);
        log.warn("Petición inválida o malformada [requestId={}] {} {} -> {}: {}",
                requestId, request.getMethod(), request.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Petición inválida", ex.getMessage(), request, requestId, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleUnexpected(
            Exception ex, HttpServletRequest request) {
        String requestId = resolveRequestId(request);
        log.error("Error inesperado en servidor [requestId={}] {} {} -> {}: {}",
                requestId, request.getMethod(), request.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", "Ha ocurrido un error inesperado al procesar su solicitud.", request, requestId, null);
    }

    private String resolveRequestId(HttpServletRequest request) {
        if (request == null) {
            return UUID.randomUUID().toString();
        }
        String requestId = request.getHeader("X-Request-Id");
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        return requestId;
    }

    private ResponseEntity<ErrorResponseDto> buildResponse(
            HttpStatus status,
            String error,
            String message,
            HttpServletRequest request,
            String requestId,
            Map<String, String> validationErrors) {
        String path = request != null ? request.getRequestURI() : "";
        ErrorResponseDto responseBody = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message != null ? message : "")
                .path(path)
                .requestId(requestId)
                .validationErrors(validationErrors)
                .build();

        return ResponseEntity.status(status)
                .header("X-Request-Id", requestId)
                .body(responseBody);
    }
}

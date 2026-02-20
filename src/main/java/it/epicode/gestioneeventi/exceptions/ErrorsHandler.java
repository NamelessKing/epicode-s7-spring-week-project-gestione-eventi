package it.epicode.gestioneeventi.exceptions;

import it.epicode.gestioneeventi.dto.response.ErrorsDTO;
import it.epicode.gestioneeventi.dto.response.ErrorsWithListDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorsHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorsWithListDTO> handleValidationException(ValidationException ex) {
        ErrorsWithListDTO response = new ErrorsWithListDTO(
                ex.getMessage(),
                LocalDateTime.now(),
                ex.getErrors()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorsWithListDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        var errors = bindingResult.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorsWithListDTO response = new ErrorsWithListDTO(
                "Validation failed",
                LocalDateTime.now(),
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorsDTO> handleBadRequestException(BadRequestException ex) {
        ErrorsDTO response = new ErrorsDTO(
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorsDTO> handleUnauthorizedException(UnauthorizedException ex) {
        ErrorsDTO response = new ErrorsDTO(
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorsDTO> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        ErrorsDTO response = new ErrorsDTO(
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorsDTO> handleNotFoundException(NotFoundException ex) {
        ErrorsDTO response = new ErrorsDTO(
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorsDTO> handleGenericException(Exception ex) {
        ex.printStackTrace();
        ErrorsDTO response = new ErrorsDTO(
                "Internal server error",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    private static final String ERROR_KEY = "error";
    private static final String MESSAGE_KEY = "message";
    private static final String TIMESTAMP_KEY = "timestamp";

    private static final String VALIDATION_ERROR = "Ошибка валидации";
    private static final String NOT_FOUND_ERROR = "Объект не найден";
    private static final String SERVER_ERROR = "Внутренняя ошибка сервера";

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(final ValidationException e) {
        Map<String, Object> body = createErrorBody(VALIDATION_ERROR, e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(final NotFoundException e) {
        Map<String, Object> body = createErrorBody(NOT_FOUND_ERROR, e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        String errorMessage = e.getFieldError() != null ? e.getFieldError().getDefaultMessage() : VALIDATION_ERROR;
        Map<String, Object> body = createErrorBody(VALIDATION_ERROR, errorMessage);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Map<String, Object>> handleThrowable(final Throwable e) {
        Map<String, Object> body = createErrorBody(SERVER_ERROR, e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Map<String, Object> createErrorBody(String error, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP_KEY, LocalDateTime.now());
        body.put(ERROR_KEY, error);
        body.put(MESSAGE_KEY, message);
        return body;
    }
}

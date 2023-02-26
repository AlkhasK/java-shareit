package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.error.exceptions.EntityNotFoundException;
import ru.practicum.shareit.error.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.error.exceptions.PermissionException;
import ru.practicum.shareit.error.exceptions.UserRestrictionException;
import ru.practicum.shareit.error.model.ErrorResponse;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.DateTimeException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, ItemNotAvailableException.class,
            DateTimeException.class, UserRestrictionException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationError(Exception validationException) {
        log.warn(validationException.getMessage());
        return new ErrorResponse(validationException.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(EntityNotFoundException entityNotFoundException) {
        log.warn(entityNotFoundException.getMessage());
        return new ErrorResponse(entityNotFoundException.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handlePermissionError(PermissionException permissionException) {
        log.warn(permissionException.getMessage());
        return new ErrorResponse(permissionException.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleServerError(Exception exception) {
        log.error("500", exception);
        var stringWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stringWriter));
        return new ErrorResponse(stringWriter.toString());
    }

}

package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.error.model.ErrorResponse;

import java.time.DateTimeException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({DateTimeException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDateTimeValidationError(Exception validationException) {
        log.warn(validationException.getMessage());
        return new ErrorResponse(validationException.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleValidationError(Exception validationException) {
        log.warn(validationException.getMessage());
        return new ErrorResponse(validationException.getMessage());
    }

}

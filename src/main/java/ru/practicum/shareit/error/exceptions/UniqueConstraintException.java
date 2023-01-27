package ru.practicum.shareit.error.exceptions;

public class UniqueConstraintException extends RuntimeException {

    public UniqueConstraintException(String message) {
        super(message);
    }

}

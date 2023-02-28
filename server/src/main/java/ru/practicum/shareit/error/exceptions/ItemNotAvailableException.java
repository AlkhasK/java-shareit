package ru.practicum.shareit.error.exceptions;

public class ItemNotAvailableException extends RuntimeException {

    public ItemNotAvailableException(String message) {
        super(message);
    }
}

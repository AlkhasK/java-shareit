package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum Status {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static Optional<Status> of(String str) {
        for (Status status : values()) {
            if (status.name().equals(str)) {
                return Optional.of(status);
            }
        }
        return Optional.empty();
    }
}

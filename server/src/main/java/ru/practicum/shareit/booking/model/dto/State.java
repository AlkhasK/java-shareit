package ru.practicum.shareit.booking.model.dto;

public enum State {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static State of(String str) {
        for (State state : values()) {
            if (state.name().equals(str)) {
                return state;
            }
        }
        return null;
    }
}

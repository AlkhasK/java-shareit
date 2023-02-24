package ru.practicum.shareit.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ControllerConstantsTest {

    @Test
    void controllerConstantsTest() {
        String userIdHeader = "X-Sharer-User-Id";
        assertThat(ControllerConstants.USER_ID_HEADER).isEqualTo(userIdHeader);
    }
}
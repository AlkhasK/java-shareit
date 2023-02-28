package ru.practicum.shareit.booking.model.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingCreateDtoTest {

    @Autowired
    private JacksonTester<BookingCreateDto> json;

    @SneakyThrows
    @Test
    void testBookingCreateDto() {
        long itemId = 1;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(itemId);
        bookingCreateDto.setStart(start);
        bookingCreateDto.setEnd(end);

        JsonContent<BookingCreateDto> result = json.write(bookingCreateDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").format(start));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").format(end));
    }
}
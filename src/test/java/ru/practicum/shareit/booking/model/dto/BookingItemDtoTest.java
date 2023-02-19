package ru.practicum.shareit.booking.model.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingItemDtoTest {

    @Autowired
    private JacksonTester<BookingItemDto> json;

    @SneakyThrows
    @Test
    void testBookingItemDto() {
        long id = 1;
        long bookerId = 2;
        Status status = Status.APPROVED;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        BookingItemDto bookingItemDto = new BookingItemDto();
        bookingItemDto.setId(id);
        bookingItemDto.setBookerId(bookerId);
        bookingItemDto.setStart(start);
        bookingItemDto.setEnd(end);
        bookingItemDto.setStatus(status);

        JsonContent<BookingItemDto> result = json.write(bookingItemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").format(start));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").format(end));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(status.name());
    }
}
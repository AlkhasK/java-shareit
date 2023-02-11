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
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @SneakyThrows
    @Test
    void testBookingDto() {
        long id = 1;
        Status status = Status.APPROVED;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(id);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setStatus(status);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").format(start));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").format(end));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(status.name());
    }

}
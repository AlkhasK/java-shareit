package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.constant.Constants;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.DateTimeException;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(Constants.USER_ID_HEADER) long userId,
                                         @Valid @RequestBody BookingCreateDto bookingCreateDto) {
        log.info("POST : create booking {}", bookingCreateDto);
        if (bookingCreateDto.getEnd().isBefore(bookingCreateDto.getStart())) {
            throw new DateTimeException(String.format("End date [%s] should be after start date [%s]",
                    bookingCreateDto.getEnd(), bookingCreateDto.getStart()));
        }
        return bookingClient.createBooking(userId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(Constants.USER_ID_HEADER) long userId,
                                          @NotNull @Positive @PathVariable Long bookingId,
                                          @NotNull @RequestParam Boolean approved) {
        log.info("PATCH : approve {} booking {}", approved, bookingId);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> find(@RequestHeader(Constants.USER_ID_HEADER) long userId,
                                       @NotNull @Positive @PathVariable Long bookingId) {
        log.info("GET : get booking id : {}", bookingId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllForUser(@RequestHeader(Constants.USER_ID_HEADER) long userId,
                                                 @NotNull @RequestParam(defaultValue = "ALL") String state,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "5") Integer size) {
        Status statusBooking = Status.of(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        log.info("GET : get bookings for user id : {} with state : {}", userId, state);
        return bookingClient.getBookings(userId, statusBooking, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllForUserItemOwner(@RequestHeader(Constants.USER_ID_HEADER) long userId,
                                                          @NotNull @RequestParam(defaultValue = "ALL") String state,
                                                          @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                          @Positive @RequestParam(defaultValue = "5") Integer size) {
        Status statusBooking = Status.of(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        log.info("GET : get bookings for user item owner id : {} with state : {}", userId, state);
        return bookingClient.getBookingsItemOwner(userId, statusBooking, from, size);
    }
}

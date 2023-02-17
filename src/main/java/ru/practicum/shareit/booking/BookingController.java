package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.utils.ControllerConstants;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader(ControllerConstants.USER_ID_HEADER) long userId,
                             @Valid @RequestBody BookingCreateDto bookingCreateDto) {
        log.info("POST : create booking {}", bookingCreateDto);
        return bookingService.createBooking(userId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader(ControllerConstants.USER_ID_HEADER) long userId,
                              @PathVariable long bookingId,
                              @RequestParam boolean approved) {
        log.info("PATCH : approve {} booking {}", approved, bookingId);
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto find(@RequestHeader(ControllerConstants.USER_ID_HEADER) long userId,
                           @PathVariable long bookingId) {
        log.info("GET : get booking id : {}", bookingId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findAllForUser(@RequestHeader(ControllerConstants.USER_ID_HEADER) long userId,
                                           @RequestParam(defaultValue = "ALL") String state,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                           @Positive @RequestParam(defaultValue = "5") int size) {
        log.info("GET : get bookings for user id : {} with state : {}", userId, state);
        return bookingService.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllForUserItemOwner(@RequestHeader(ControllerConstants.USER_ID_HEADER) long userId,
                                                    @RequestParam(defaultValue = "ALL") String state,
                                                    @RequestParam(defaultValue = "0") int from,
                                                    @RequestParam(defaultValue = "5") int size) {
        log.info("GET : get bookings for user item owner id : {} with state : {}", userId, state);
        return bookingService.getBookingsItemOwner(userId, state, from, size);
    }
}

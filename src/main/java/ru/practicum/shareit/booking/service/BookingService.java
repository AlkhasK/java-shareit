package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(long userId, BookingCreateDto bookingCreateDto);

    BookingDto approveBooking(long userId, long bookingId, boolean status);

    BookingDto getBooking(long userId, long bookingId);

    List<BookingDto> getBookings(long userId, String stateParam);

    List<BookingDto> getBookingsItemOwner(long userId, String stateParam);

}

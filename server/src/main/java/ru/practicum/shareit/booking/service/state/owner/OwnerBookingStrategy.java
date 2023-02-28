package ru.practicum.shareit.booking.service.state.owner;

import org.springframework.data.domain.Page;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.State;
import ru.practicum.shareit.booking.service.state.BookingParams;

public interface OwnerBookingStrategy {
    Page<Booking> getBookings(BookingParams params);

    State getState();
}

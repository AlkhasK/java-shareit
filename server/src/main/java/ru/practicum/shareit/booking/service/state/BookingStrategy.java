package ru.practicum.shareit.booking.service.state;

import org.springframework.data.domain.Page;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.State;

public interface BookingStrategy {
    Page<Booking> getBookings(BookingParams params);

    State getState();
}

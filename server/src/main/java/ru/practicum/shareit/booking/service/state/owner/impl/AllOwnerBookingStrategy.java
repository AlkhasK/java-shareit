package ru.practicum.shareit.booking.service.state.owner.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.State;
import ru.practicum.shareit.booking.service.state.BookingParams;
import ru.practicum.shareit.booking.service.state.owner.OwnerBookingStrategy;
import ru.practicum.shareit.booking.storage.BookingRepository;

@Component
@RequiredArgsConstructor
public class AllOwnerBookingStrategy implements OwnerBookingStrategy {

    private final BookingRepository bookingRepository;

    @Override
    public Page<Booking> getBookings(BookingParams params) {
        return bookingRepository.findAllByItem_Owner_Id(params.getUserId(), params.getPageable());
    }

    @Override
    public State getState() {
        return State.ALL;
    }
}

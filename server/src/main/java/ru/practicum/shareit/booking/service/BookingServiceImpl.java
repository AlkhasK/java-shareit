package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingMapper;
import ru.practicum.shareit.booking.model.dto.State;
import ru.practicum.shareit.booking.service.state.BookingParams;
import ru.practicum.shareit.booking.service.state.BookingStrategyFactory;
import ru.practicum.shareit.booking.service.state.owner.OwnerBookingStrategyFactory;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.error.exceptions.EntityNotFoundException;
import ru.practicum.shareit.error.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.pagination.PageRequestWithOffset;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserService userService;

    private final BookingMapper bookingMapper;

    private final BookingStrategyFactory bookingStrategyFactory;

    private final OwnerBookingStrategyFactory ownerBookingStrategyFactory;

    @Override
    @Transactional
    public BookingDto createBooking(long userId, BookingCreateDto bookingCreateDto) {
        User booker = userService.getUser(userId);
        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("No item with id : " + bookingCreateDto.getItemId()));
        Booking booking = bookingMapper.toBooking(bookingCreateDto, item, booker);
        if (booking.getBooker().equals(booking.getItem().getOwner())) {
            throw new EntityNotFoundException("Owner can't book item");
        }
        if (!item.getAvailable()) {
            throw new ItemNotAvailableException(String.format("Item id : %s is booked", item.getId()));
        }
        Booking createdBooking = bookingRepository.save(booking);
        return bookingMapper.toBookingDto(createdBooking);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(long userId, long bookingId, boolean status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("No booking with id : " + bookingId));
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new IllegalArgumentException("Can't change status of approved booking");
        }
        User owner = userService.getUser(userId);
        Item item = booking.getItem();
        if (!item.getOwner().equals(owner)) {
            throw new EntityNotFoundException(String.format("No entity booking id : %s for user id : %s",
                    booking.getId(), owner.getId()));
        }
        if (status) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        Booking createdBooking = bookingRepository.save(booking);
        return bookingMapper.toBookingDto(createdBooking);
    }

    @Override
    public BookingDto getBooking(long userId, long bookingId) {
        User user = userService.getUser(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("No booking with id : " + bookingId));
        Item item = booking.getItem();
        if (!booking.getBooker().equals(user) && !item.getOwner().equals(user)) {
            throw new EntityNotFoundException(String.format("No entity booking id : %s for user id : %s",
                    booking.getId(), user.getId()));
        }
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookings(long userId, String stateParam, int from, int size) {
        userService.getUser(userId);
        State state = State.of(stateParam);
        if (Objects.isNull(state)) {
            throw new IllegalArgumentException("Unknown state: " + stateParam);
        }
        Pageable pageable = PageRequestWithOffset.of(from, size, sort);
        BookingParams bookingParams = new BookingParams(userId, pageable);
        Page<Booking> bookings = bookingStrategyFactory.getStrategy(state).getBookings(bookingParams);
        return bookings.map(bookingMapper::toBookingDto).getContent();
    }

    @Override
    public List<BookingDto> getBookingsItemOwner(long userId, String stateParam, int from, int size) {
        userService.getUser(userId);
        State state = State.of(stateParam);
        if (Objects.isNull(state)) {
            throw new IllegalArgumentException("Unknown state: " + stateParam);
        }
        Pageable pageable = PageRequestWithOffset.of(from, size, sort);
        BookingParams bookingParams = new BookingParams(userId, pageable);
        Page<Booking> bookings = ownerBookingStrategyFactory.getStrategy(state).getBookings(bookingParams);
        return bookings.map(bookingMapper::toBookingDto).getContent();
    }

}

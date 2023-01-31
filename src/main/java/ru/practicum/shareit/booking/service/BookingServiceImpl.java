package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingMapper;
import ru.practicum.shareit.booking.model.dto.State;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.error.exceptions.EntityNotFoundException;
import ru.practicum.shareit.error.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserService userService;

    private final BookingMapper bookingMapper;

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
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new DateTimeException(String.format("End date [%s] should be after start date [%s]",
                    booking.getEnd(), booking.getStart()));
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
    public List<BookingDto> getBookings(long userId, String stateParam) {
        userService.getUser(userId);
        State state = State.of(stateParam);
        if (Objects.isNull(state)) {
            throw new IllegalArgumentException("Unknown state: " + stateParam);
        }
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBooker_Id(userId, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBooker_IdAndStatusInAndStartIsBeforeAndEndIsAfter(userId,
                        List.of(Status.APPROVED, Status.WAITING, Status.REJECTED), LocalDateTime.now(), LocalDateTime.now(), sort);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBooker_IdAndStatusInAndEndIsBefore(userId,
                        List.of(Status.APPROVED, Status.WAITING), LocalDateTime.now(), sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBooker_IdAndStatusInAndStartIsAfter(userId,
                        List.of(Status.APPROVED, Status.WAITING), LocalDateTime.now(), sort);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBooker_IdAndStatus(userId, Status.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBooker_IdAndStatus(userId, Status.REJECTED, sort);
                break;
        }
        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsItemOwner(long userId, String stateParam) {
        userService.getUser(userId);
        State state = State.of(stateParam);
        if (Objects.isNull(state)) {
            throw new IllegalArgumentException("Unknown state: " + stateParam);
        }
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItem_Owner_Id(userId, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStatusInAndStartIsBeforeAndEndIsAfter(userId,
                        List.of(Status.APPROVED, Status.WAITING, Status.REJECTED), LocalDateTime.now(), LocalDateTime.now(), sort);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStatusInAndEndIsBefore(userId,
                        List.of(Status.APPROVED, Status.WAITING), LocalDateTime.now(), sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStatusInAndStartIsAfter(userId,
                        List.of(Status.APPROVED, Status.WAITING), LocalDateTime.now(), sort);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStatus(userId, Status.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStatus(userId, Status.REJECTED, sort);
                break;
        }
        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

}

package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingMapper;
import ru.practicum.shareit.booking.service.state.BookingStrategy;
import ru.practicum.shareit.booking.service.state.BookingStrategyFactory;
import ru.practicum.shareit.booking.service.state.owner.OwnerBookingStrategy;
import ru.practicum.shareit.booking.service.state.owner.OwnerBookingStrategyFactory;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.error.exceptions.EntityNotFoundException;
import ru.practicum.shareit.error.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private BookingStrategyFactory bookingStrategyFactory;

    @Mock
    private OwnerBookingStrategyFactory ownerBookingStrategyFactory;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void createBookingWhenUserAndBookingCreateDtoValidThenReturnBookingDto() {
        long userId = 0L;
        User user = new User();

        long itemId = 0L;
        boolean itemAvailable = true;
        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(itemAvailable);
        item.setOwner(user);

        User booker = new User();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(itemId);

        long bookingDtoId = 0L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingDtoId);

        Mockito.when(userService.getUser(userId))
                .thenReturn(user);
        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        Mockito.when(bookingMapper.toBooking(bookingCreateDto, item, user))
                .thenReturn(booking);
        Mockito.when(bookingRepository.save(booking))
                .thenReturn(booking);
        Mockito.when(bookingMapper.toBookingDto(booking))
                .thenReturn(bookingDto);

        BookingDto bookingDtoReturned = bookingService.createBooking(userId, bookingCreateDto);

        assertThat(bookingDtoReturned).isEqualTo(bookingDto);
    }

    @Test
    void createBookingWhenItemNotExistsReturnInterrupt() {
        long userId = 0L;
        User user = new User();

        long itemId = 0L;

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(itemId);

        long bookingDtoId = 0L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingDtoId);

        Mockito.when(userService.getUser(userId))
                .thenReturn(user);
        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.createBooking(userId, bookingCreateDto));

        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void createBookingWhenBookerAndOwnerTheSameReturnInterrupt() {
        long userId = 0L;
        User user = new User();

        long itemId = 0L;
        boolean itemAvailable = true;
        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(itemAvailable);
        item.setOwner(user);

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(itemId);

        long bookingDtoId = 0L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingDtoId);

        Mockito.when(userService.getUser(userId))
                .thenReturn(user);
        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        Mockito.when(bookingMapper.toBooking(bookingCreateDto, item, user))
                .thenReturn(booking);

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.createBooking(userId, bookingCreateDto));

        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void createBookingWhenItemNotAvailableReturnInterrupt() {
        long userId = 0L;
        User user = new User();

        long itemId = 0L;
        boolean itemAvailable = false;
        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(itemAvailable);
        item.setOwner(user);

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        User booker = new User();
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(itemId);

        long bookingDtoId = 0L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingDtoId);

        Mockito.when(userService.getUser(userId))
                .thenReturn(user);
        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        Mockito.when(bookingMapper.toBooking(bookingCreateDto, item, user))
                .thenReturn(booking);

        assertThrows(ItemNotAvailableException.class,
                () -> bookingService.createBooking(userId, bookingCreateDto));

        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void approveBookingWhenStatusTrueThenApproved() {
        boolean status = true;
        long userId = 0L;
        User user = new User();

        long itemId = 0L;
        boolean itemAvailable = true;
        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(itemAvailable);
        item.setOwner(user);

        long bookingId = 1L;
        User booker = new User();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        Status bookingStatus = Status.WAITING;
        Booking spyBooking = Mockito.spy(new Booking());
        spyBooking.setId(bookingId);
        spyBooking.setBooker(booker);
        spyBooking.setStart(start);
        spyBooking.setEnd(end);
        spyBooking.setItem(item);
        spyBooking.setStatus(bookingStatus);

        long bookingDtoId = 0L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingDtoId);

        Mockito.when(userService.getUser(userId))
                .thenReturn(user);
        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(spyBooking));
        Mockito.when(bookingRepository.save(spyBooking))
                .thenReturn(spyBooking);
        Mockito.when(bookingMapper.toBookingDto(spyBooking))
                .thenReturn(bookingDto);

        BookingDto bookingDtoReturned = bookingService.approveBooking(userId, bookingId, status);

        assertThat(bookingDtoReturned).isEqualTo(bookingDto);
        Mockito.verify(spyBooking).setStatus(Status.APPROVED);
    }

    @Test
    void approveBookingWhenStatusFalseThenRejected() {
        boolean status = false;
        long userId = 0L;
        User user = new User();

        long itemId = 0L;
        boolean itemAvailable = true;
        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(itemAvailable);
        item.setOwner(user);

        long bookingId = 1L;
        User booker = new User();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        Status bookingStatus = Status.WAITING;
        Booking spyBooking = Mockito.spy(new Booking());
        spyBooking.setId(bookingId);
        spyBooking.setBooker(booker);
        spyBooking.setStart(start);
        spyBooking.setEnd(end);
        spyBooking.setItem(item);
        spyBooking.setStatus(bookingStatus);

        long bookingDtoId = 0L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingDtoId);

        Mockito.when(userService.getUser(userId))
                .thenReturn(user);
        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(spyBooking));
        Mockito.when(bookingRepository.save(spyBooking))
                .thenReturn(spyBooking);
        Mockito.when(bookingMapper.toBookingDto(spyBooking))
                .thenReturn(bookingDto);

        BookingDto bookingDtoReturned = bookingService.approveBooking(userId, bookingId, status);

        assertThat(bookingDtoReturned).isEqualTo(bookingDto);
        Mockito.verify(spyBooking).setStatus(Status.REJECTED);
    }

    @Test
    void approveBookingWhenInvalidBookingThenInterrupt() {
        boolean status = true;
        long userId = 0L;
        long bookingId = 1L;
        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.approveBooking(userId, bookingId, status));

        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void approveBookingWhenStatusApprovedThenInterrupt() {
        boolean status = false;
        long userId = 0L;
        User user = new User();

        long itemId = 0L;
        boolean itemAvailable = true;
        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(itemAvailable);
        item.setOwner(user);

        long bookingId = 1L;
        User booker = new User();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        Status bookingStatus = Status.APPROVED;
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setStatus(bookingStatus);

        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.approveBooking(userId, bookingId, status));

        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void approveBookingWhenUserNotOwnerThenInterrupt() {
        boolean status = false;
        long userId = 0L;
        User user = new User();

        long itemId = 0L;
        boolean itemAvailable = true;
        User owner = new User();
        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(itemAvailable);
        item.setOwner(owner);

        long bookingId = 1L;
        User booker = new User();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        Status bookingStatus = Status.WAITING;
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setStatus(bookingStatus);

        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));
        Mockito.when(userService.getUser(userId))
                .thenReturn(user);

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.approveBooking(userId, bookingId, status));

        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void getBookingWhenBookingExistsThenReturnBookingDto() {
        long userId = 0L;
        User user = new User();

        long itemId = 0L;
        boolean itemAvailable = true;
        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(itemAvailable);
        item.setOwner(user);

        long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        Status bookingStatus = Status.WAITING;
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(user);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setStatus(bookingStatus);

        long bookingDtoId = 0L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingDtoId);

        Mockito.when(userService.getUser(userId))
                .thenReturn(user);
        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));
        Mockito.when(bookingMapper.toBookingDto(booking))
                .thenReturn(bookingDto);

        BookingDto bookingDtoReturned = bookingService.getBooking(userId, bookingId);

        assertThat(bookingDtoReturned).isEqualTo(bookingDto);
    }

    @Test
    void getBookingWhenUserInvalidThenInterrupt() {
        long userId = 0L;
        User user = new User();

        long itemId = 0L;
        boolean itemAvailable = true;
        User owner = new User();
        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(itemAvailable);
        item.setOwner(owner);

        long bookingId = 1L;
        User booker = new User();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        Status bookingStatus = Status.WAITING;
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setStatus(bookingStatus);

        Mockito.when(userService.getUser(userId))
                .thenReturn(user);
        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBooking(userId, bookingId));
    }

    @Test
    void getBookingWhenBookingNotExistThenInterrupt() {
        long userId = 0L;
        User user = new User();

        long itemId = 0L;
        boolean itemAvailable = true;
        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(itemAvailable);
        item.setOwner(user);

        long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        Status bookingStatus = Status.WAITING;
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(user);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setStatus(bookingStatus);

        Mockito.when(userService.getUser(userId))
                .thenReturn(user);
        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBooking(userId, bookingId));
    }

    @Test
    void getBookingsWhenStateAllThenReturnBookingDto() {
        int from = 0;
        int size = 1;
        String state = "ALL";
        long userId = 0L;
        User user = new User();

        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);
        Page<Booking> pageBookings = new PageImpl<>(bookings);

        BookingDto bookingDto = new BookingDto();
        List<BookingDto> bookingDtos = List.of(bookingDto);

        BookingStrategy bookingStrategy = Mockito.mock(BookingStrategy.class);

        Mockito.when(userService.getUser(userId))
                .thenReturn(user);
        Mockito.when(bookingStrategyFactory.getStrategy(Mockito.any()))
                .thenReturn(bookingStrategy);
        Mockito.when(bookingStrategy.getBookings(Mockito.any()))
                .thenReturn(pageBookings);
        Mockito.when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> bookingDtoReturned = bookingService.getBookings(userId, state, from, size);

        assertThat(bookingDtoReturned).isEqualTo(bookingDtos);
    }

    @Test
    void getBookingsWhenStateCurrentThenReturnBookingDto() {
        int from = 0;
        int size = 1;
        String state = "CURRENT";
        long userId = 0L;
        User user = new User();

        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);
        Page<Booking> pageBookings = new PageImpl<>(bookings);

        BookingDto bookingDto = new BookingDto();
        List<BookingDto> bookingDtos = List.of(bookingDto);

        BookingStrategy bookingStrategy = Mockito.mock(BookingStrategy.class);

        Mockito.when(userService.getUser(userId))
                .thenReturn(user);
        Mockito.when(bookingStrategyFactory.getStrategy(Mockito.any()))
                .thenReturn(bookingStrategy);
        Mockito.when(bookingStrategy.getBookings(Mockito.any()))
                .thenReturn(pageBookings);
        Mockito.when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> bookingDtoReturned = bookingService.getBookings(userId, state, from, size);

        assertThat(bookingDtoReturned).isEqualTo(bookingDtos);
    }

    @Test
    void getBookingsWhenStatePastThenReturnBookingDto() {
        int from = 0;
        int size = 1;
        String state = "PAST";
        long userId = 0L;
        User user = new User();

        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);
        Page<Booking> pageBookings = new PageImpl<>(bookings);

        BookingDto bookingDto = new BookingDto();
        List<BookingDto> bookingDtos = List.of(bookingDto);

        BookingStrategy bookingStrategy = Mockito.mock(BookingStrategy.class);

        Mockito.when(userService.getUser(userId))
                .thenReturn(user);
        Mockito.when(bookingStrategyFactory.getStrategy(Mockito.any()))
                .thenReturn(bookingStrategy);
        Mockito.when(bookingStrategy.getBookings(Mockito.any()))
                .thenReturn(pageBookings);
        Mockito.when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> bookingDtoReturned = bookingService.getBookings(userId, state, from, size);

        assertThat(bookingDtoReturned).isEqualTo(bookingDtos);
    }

    @Test
    void getBookingsWhenStateFutureThenReturnBookingDto() {
        int from = 0;
        int size = 1;
        String state = "FUTURE";
        long userId = 0L;
        User user = new User();

        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);
        Page<Booking> pageBookings = new PageImpl<>(bookings);

        BookingDto bookingDto = new BookingDto();
        List<BookingDto> bookingDtos = List.of(bookingDto);

        BookingStrategy bookingStrategy = Mockito.mock(BookingStrategy.class);

        Mockito.when(userService.getUser(userId))
                .thenReturn(user);
        Mockito.when(bookingStrategyFactory.getStrategy(Mockito.any()))
                .thenReturn(bookingStrategy);
        Mockito.when(bookingStrategy.getBookings(Mockito.any()))
                .thenReturn(pageBookings);
        Mockito.when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> bookingDtoReturned = bookingService.getBookings(userId, state, from, size);

        assertThat(bookingDtoReturned).isEqualTo(bookingDtos);
    }

    @Test
    void getBookingsWhenStateWaitingThenReturnBookingDto() {
        int from = 0;
        int size = 1;
        String state = "WAITING";
        long userId = 0L;
        User user = new User();

        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);
        Page<Booking> pageBookings = new PageImpl<>(bookings);

        BookingDto bookingDto = new BookingDto();
        List<BookingDto> bookingDtos = List.of(bookingDto);

        BookingStrategy bookingStrategy = Mockito.mock(BookingStrategy.class);

        Mockito.when(userService.getUser(userId))
                .thenReturn(user);
        Mockito.when(bookingStrategyFactory.getStrategy(Mockito.any()))
                .thenReturn(bookingStrategy);
        Mockito.when(bookingStrategy.getBookings(Mockito.any()))
                .thenReturn(pageBookings);
        Mockito.when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> bookingDtoReturned = bookingService.getBookings(userId, state, from, size);

        assertThat(bookingDtoReturned).isEqualTo(bookingDtos);
    }

    @Test
    void getBookingsWhenStateRejectedThenReturnBookingDto() {
        int from = 0;
        int size = 1;
        String state = "REJECTED";
        long userId = 0L;
        User user = new User();

        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);
        Page<Booking> pageBookings = new PageImpl<>(bookings);

        BookingDto bookingDto = new BookingDto();
        List<BookingDto> bookingDtos = List.of(bookingDto);

        BookingStrategy bookingStrategy = Mockito.mock(BookingStrategy.class);

        Mockito.when(userService.getUser(userId))
                .thenReturn(user);
        Mockito.when(bookingStrategyFactory.getStrategy(Mockito.any()))
                .thenReturn(bookingStrategy);
        Mockito.when(bookingStrategy.getBookings(Mockito.any()))
                .thenReturn(pageBookings);
        Mockito.when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> bookingDtoReturned = bookingService.getBookings(userId, state, from, size);

        assertThat(bookingDtoReturned).isEqualTo(bookingDtos);
    }

    @Test
    void getBookingsWhenStateInvalidThenInterrupt() {
        int from = 0;
        int size = 1;
        String state = "INVALID";
        long userId = 0L;
        User user = new User();

        Mockito.when(userService.getUser(userId))
                .thenReturn(user);

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.getBookings(userId, state, from, size));
    }

    @Test
    void getBookingsItemOwnerWhenStateAllThenReturnBookingDto() {
        int from = 0;
        int size = 1;
        String state = "ALL";
        long userId = 0L;
        User user = new User();

        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);
        Page<Booking> pageBookings = new PageImpl<>(bookings);

        BookingDto bookingDto = new BookingDto();
        List<BookingDto> bookingDtos = List.of(bookingDto);

        OwnerBookingStrategy ownerBookingStrategy = Mockito.mock(OwnerBookingStrategy.class);

        Mockito.when(userService.getUser(userId))
                .thenReturn(user);
        Mockito.when(ownerBookingStrategyFactory.getStrategy(Mockito.any()))
                .thenReturn(ownerBookingStrategy);
        Mockito.when(ownerBookingStrategy.getBookings(Mockito.any()))
                .thenReturn(pageBookings);
        Mockito.when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> bookingDtoReturned = bookingService.getBookingsItemOwner(userId, state, from, size);

        assertThat(bookingDtoReturned).isEqualTo(bookingDtos);
    }

    @Test
    void getBookingsItemOwnerWhenStateCurrentThenReturnBookingDto() {
        int from = 0;
        int size = 1;
        String state = "CURRENT";
        long userId = 0L;
        User user = new User();

        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);
        Page<Booking> pageBookings = new PageImpl<>(bookings);

        BookingDto bookingDto = new BookingDto();
        List<BookingDto> bookingDtos = List.of(bookingDto);

        OwnerBookingStrategy ownerBookingStrategy = Mockito.mock(OwnerBookingStrategy.class);

        Mockito.when(userService.getUser(userId))
                .thenReturn(user);
        Mockito.when(ownerBookingStrategyFactory.getStrategy(Mockito.any()))
                .thenReturn(ownerBookingStrategy);
        Mockito.when(ownerBookingStrategy.getBookings(Mockito.any()))
                .thenReturn(pageBookings);
        Mockito.when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> bookingDtoReturned = bookingService.getBookingsItemOwner(userId, state, from, size);

        assertThat(bookingDtoReturned).isEqualTo(bookingDtos);
    }

    @Test
    void getBookingsItemOwnerWhenStatePastThenReturnBookingDto() {
        int from = 0;
        int size = 1;
        String state = "PAST";
        long userId = 0L;
        User user = new User();

        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);
        Page<Booking> pageBookings = new PageImpl<>(bookings);

        BookingDto bookingDto = new BookingDto();
        List<BookingDto> bookingDtos = List.of(bookingDto);

        OwnerBookingStrategy ownerBookingStrategy = Mockito.mock(OwnerBookingStrategy.class);

        Mockito.when(userService.getUser(userId))
                .thenReturn(user);
        Mockito.when(ownerBookingStrategyFactory.getStrategy(Mockito.any()))
                .thenReturn(ownerBookingStrategy);
        Mockito.when(ownerBookingStrategy.getBookings(Mockito.any()))
                .thenReturn(pageBookings);
        Mockito.when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> bookingDtoReturned = bookingService.getBookingsItemOwner(userId, state, from, size);

        assertThat(bookingDtoReturned).isEqualTo(bookingDtos);
    }

    @Test
    void getBookingsItemOwnerWhenStateFutureThenReturnBookingDto() {
        int from = 0;
        int size = 1;
        String state = "FUTURE";
        long userId = 0L;
        User user = new User();

        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);
        Page<Booking> pageBookings = new PageImpl<>(bookings);

        BookingDto bookingDto = new BookingDto();
        List<BookingDto> bookingDtos = List.of(bookingDto);

        OwnerBookingStrategy ownerBookingStrategy = Mockito.mock(OwnerBookingStrategy.class);

        Mockito.when(userService.getUser(userId))
                .thenReturn(user);
        Mockito.when(ownerBookingStrategyFactory.getStrategy(Mockito.any()))
                .thenReturn(ownerBookingStrategy);
        Mockito.when(ownerBookingStrategy.getBookings(Mockito.any()))
                .thenReturn(pageBookings);
        Mockito.when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> bookingDtoReturned = bookingService.getBookingsItemOwner(userId, state, from, size);

        assertThat(bookingDtoReturned).isEqualTo(bookingDtos);
    }

    @Test
    void getBookingsItemOwnerWhenStateWaitingThenReturnBookingDto() {
        int from = 0;
        int size = 1;
        String state = "WAITING";
        long userId = 0L;
        User user = new User();

        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);
        Page<Booking> pageBookings = new PageImpl<>(bookings);

        BookingDto bookingDto = new BookingDto();
        List<BookingDto> bookingDtos = List.of(bookingDto);

        OwnerBookingStrategy ownerBookingStrategy = Mockito.mock(OwnerBookingStrategy.class);

        Mockito.when(userService.getUser(userId))
                .thenReturn(user);
        Mockito.when(ownerBookingStrategyFactory.getStrategy(Mockito.any()))
                .thenReturn(ownerBookingStrategy);
        Mockito.when(ownerBookingStrategy.getBookings(Mockito.any()))
                .thenReturn(pageBookings);
        Mockito.when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> bookingDtoReturned = bookingService.getBookingsItemOwner(userId, state, from, size);

        assertThat(bookingDtoReturned).isEqualTo(bookingDtos);
    }

    @Test
    void getBookingsItemOwnerWhenStateRejectedThenReturnBookingDto() {
        int from = 0;
        int size = 1;
        String state = "REJECTED";
        long userId = 0L;
        User user = new User();

        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);
        Page<Booking> pageBookings = new PageImpl<>(bookings);

        BookingDto bookingDto = new BookingDto();
        List<BookingDto> bookingDtos = List.of(bookingDto);

        OwnerBookingStrategy ownerBookingStrategy = Mockito.mock(OwnerBookingStrategy.class);

        Mockito.when(userService.getUser(userId))
                .thenReturn(user);
        Mockito.when(ownerBookingStrategyFactory.getStrategy(Mockito.any()))
                .thenReturn(ownerBookingStrategy);
        Mockito.when(ownerBookingStrategy.getBookings(Mockito.any()))
                .thenReturn(pageBookings);
        Mockito.when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> bookingDtoReturned = bookingService.getBookingsItemOwner(userId, state, from, size);

        assertThat(bookingDtoReturned).isEqualTo(bookingDtos);
    }

    @Test
    void getBookingsItemOwnerWhenStateInvalidThenReturnBookingDto() {
        int from = 0;
        int size = 1;
        String state = "INVALID";
        long userId = 0L;
        User user = new User();

        Mockito.when(userService.getUser(userId))
                .thenReturn(user);

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.getBookingsItemOwner(userId, state, from, size));
    }
}
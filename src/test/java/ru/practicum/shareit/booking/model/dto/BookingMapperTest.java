package ru.practicum.shareit.booking.model.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserMapper;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingMapperTest {

    private final BookingMapper bookingMapper = new BookingMapper();

    @Test
    void toBookingTest() {
        Status status = Status.WAITING;

        long bookingItemId = 0L;
        LocalDateTime bookingStart = LocalDateTime.now();
        LocalDateTime bookingEnd = LocalDateTime.now().plusHours(1);
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(bookingItemId);
        bookingCreateDto.setStart(bookingStart);
        bookingCreateDto.setEnd(bookingEnd);

        User itemOwner = new User();
        ItemRequest itemRequest = new ItemRequest();
        boolean itemAvailable = true;
        String itemDescription = "description";
        String itemName = "name";
        long itemId = 0L;
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(itemOwner);
        item.setAvailable(itemAvailable);
        item.setName(itemName);
        item.setDescription(itemDescription);
        item.setRequest(itemRequest);

        long userId = 1L;
        String userName = "name";
        String userEmail = "email";
        User booker = new User();
        booker.setId(userId);
        booker.setName(userName);
        booker.setEmail(userEmail);

        Booking booking = bookingMapper.toBooking(bookingCreateDto, item, booker);

        assertThat(booking.getStart()).isEqualTo(bookingStart);
        assertThat(booking.getEnd()).isEqualTo(bookingEnd);
        assertThat(booking.getItem()).isEqualTo(item);
        assertThat(booking.getBooker()).isEqualTo(booker);
        assertThat(booking.getStatus()).isEqualTo(status);
    }

    @Test
    void toBookingDtoTest() {
        long bookingId = 0L;
        LocalDateTime bookingStart = LocalDateTime.now();
        LocalDateTime bookingEnd = LocalDateTime.now().plusHours(1);
        Status bookingStatus = Status.APPROVED;

        User itemOwner = new User();
        ItemRequest itemRequest = new ItemRequest();
        boolean itemAvailable = true;
        String itemDescription = "description";
        String itemName = "name";
        long itemId = 0L;
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(itemOwner);
        item.setAvailable(itemAvailable);
        item.setName(itemName);
        item.setDescription(itemDescription);
        item.setRequest(itemRequest);

        long userId = 1L;
        String userName = "name";
        String userEmail = "email";
        User booker = new User();
        booker.setId(userId);
        booker.setName(userName);
        booker.setEmail(userEmail);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStart(bookingStart);
        booking.setEnd(bookingEnd);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(bookingStatus);

        BookingDto bookingDto = bookingMapper.toBookingDto(booking);

        assertThat(bookingDto.getId()).isEqualTo(bookingId);
        assertThat(bookingDto.getStart()).isEqualTo(bookingStart);
        assertThat(bookingDto.getEnd()).isEqualTo(bookingEnd);
        assertThat(bookingDto.getItem()).isEqualTo(ItemMapper.toItemDto(item));
        assertThat(bookingDto.getBooker()).isEqualTo(UserMapper.toUserDto(booker));
        assertThat(bookingDto.getStatus()).isEqualTo(bookingStatus);
    }

    @Test
    void toBookingItemDtoTest() {
        long bookingId = 0L;
        LocalDateTime bookingStart = LocalDateTime.now();
        LocalDateTime bookingEnd = LocalDateTime.now().plusHours(1);
        Status bookingStatus = Status.APPROVED;

        User itemOwner = new User();
        ItemRequest itemRequest = new ItemRequest();
        boolean itemAvailable = true;
        String itemDescription = "description";
        String itemName = "name";
        long itemId = 0L;
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(itemOwner);
        item.setAvailable(itemAvailable);
        item.setName(itemName);
        item.setDescription(itemDescription);
        item.setRequest(itemRequest);

        long userId = 1L;
        String userName = "name";
        String userEmail = "email";
        User booker = new User();
        booker.setId(userId);
        booker.setName(userName);
        booker.setEmail(userEmail);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStart(bookingStart);
        booking.setEnd(bookingEnd);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(bookingStatus);

        BookingItemDto bookingItemDto = bookingMapper.toBookingItemDto(booking);

        assertThat(bookingItemDto.getId()).isEqualTo(bookingId);
        assertThat(bookingItemDto.getStart()).isEqualTo(bookingStart);
        assertThat(bookingItemDto.getEnd()).isEqualTo(bookingEnd);
        assertThat(bookingItemDto.getItem()).isEqualTo(ItemMapper.toItemDto(item));
        assertThat(bookingItemDto.getBookerId()).isEqualTo(userId);
        assertThat(bookingItemDto.getStatus()).isEqualTo(bookingStatus);
    }
}
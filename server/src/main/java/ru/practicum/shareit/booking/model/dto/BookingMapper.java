package ru.practicum.shareit.booking.model.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.model.dto.UserMapper;

@Component
public class BookingMapper {

    public Booking toBooking(BookingCreateDto bookingCreateDto, Item item, User booker) {
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(bookingCreateDto.getStart());
        booking.setEnd(bookingCreateDto.getEnd());
        booking.setStatus(Status.WAITING);
        return booking;
    }

    public BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        ItemDto itemDto = ItemMapper.toItemDto(booking.getItem());
        bookingDto.setItem(itemDto);
        UserDto userDto = UserMapper.toUserDto(booking.getBooker());
        bookingDto.setBooker(userDto);
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public BookingItemDto toBookingItemDto(Booking booking) {
        BookingItemDto bookingItemDto = new BookingItemDto();
        bookingItemDto.setId(booking.getId());
        bookingItemDto.setStart(booking.getStart());
        bookingItemDto.setEnd(booking.getEnd());
        ItemDto itemDto = ItemMapper.toItemDto(booking.getItem());
        bookingItemDto.setItem(itemDto);
        bookingItemDto.setBookerId(booking.getBooker().getId());
        bookingItemDto.setStatus(booking.getStatus());
        return bookingItemDto;
    }

}

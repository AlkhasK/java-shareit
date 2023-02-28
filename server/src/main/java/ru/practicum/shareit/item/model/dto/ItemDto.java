package ru.practicum.shareit.item.model.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.model.dto.BookingItemDto;
import ru.practicum.shareit.item.comment.model.dto.CommentDto;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    @EqualsAndHashCode.Exclude
    private Long requestId;
    @EqualsAndHashCode.Exclude
    private BookingItemDto lastBooking;
    @EqualsAndHashCode.Exclude
    private BookingItemDto nextBooking;
    @EqualsAndHashCode.Exclude
    private List<CommentDto> comments;
}

package ru.practicum.shareit.booking.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.dto.ItemDto;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class BookingItemDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDto item;
    private Long bookerId;
    private Status status;
}

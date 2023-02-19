package ru.practicum.shareit.item.model.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.model.dto.BookingItemDto;
import ru.practicum.shareit.item.comment.model.dto.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ItemDto {
    @EqualsAndHashCode.Exclude
    private Long id;
    @NotBlank
    @Size(max = 30)
    private String name;
    @NotBlank
    @Size(max = 200)
    private String description;
    @NotNull
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

package ru.practicum.shareit.item.model.dto;

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
public class ItemDto {
    private Long id;
    @NotBlank
    @Size(max = 30)
    private String name;
    @NotBlank
    @Size(max = 200)
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;
    private List<CommentDto> comments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemDto)) return false;

        ItemDto itemDto = (ItemDto) o;

        if (!getName().equals(itemDto.getName())) return false;
        if (!getDescription().equals(itemDto.getDescription())) return false;
        return getAvailable().equals(itemDto.getAvailable());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getDescription().hashCode();
        result = 31 * result + getAvailable().hashCode();
        return result;
    }
}

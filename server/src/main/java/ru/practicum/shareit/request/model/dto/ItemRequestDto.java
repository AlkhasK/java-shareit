package ru.practicum.shareit.request.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class ItemRequestDto {
    private Long id;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created;
    private List<ItemDto> items;

    @Getter
    @Setter
    @ToString
    public static class ItemDto {
        private Long id;
        private String name;
        private Long ownerId;
        private String description;
        private Boolean available;
        private Long requestId;
    }
}

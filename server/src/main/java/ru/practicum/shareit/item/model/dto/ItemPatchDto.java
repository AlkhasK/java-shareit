package ru.practicum.shareit.item.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ItemPatchDto {
    private String name;
    private String description;
    private Boolean available;
}

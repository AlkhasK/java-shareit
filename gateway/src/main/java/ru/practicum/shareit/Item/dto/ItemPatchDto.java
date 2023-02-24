package ru.practicum.shareit.Item.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
public class ItemPatchDto {
    @Size(max = 30)
    private String name;
    @Size(max = 200)
    private String description;
    private Boolean available;
}

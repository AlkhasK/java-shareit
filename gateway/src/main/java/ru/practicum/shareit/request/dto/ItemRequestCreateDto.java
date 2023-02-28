package ru.practicum.shareit.request.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ItemRequestCreateDto {
    @Size(max = 512)
    @NotBlank
    private String description;
}

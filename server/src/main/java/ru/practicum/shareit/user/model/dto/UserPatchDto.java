package ru.practicum.shareit.user.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserPatchDto {
    private String name;
    private String email;
}

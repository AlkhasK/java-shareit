package ru.practicum.shareit.user.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDto {
    @ToString.Exclude
    private Long id;
    private String name;
    private String email;
}

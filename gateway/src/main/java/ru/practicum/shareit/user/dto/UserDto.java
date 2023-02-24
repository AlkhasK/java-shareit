package ru.practicum.shareit.user.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UserDto {
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Long id;
    @NotBlank
    @Size(max = 30)
    private String name;
    @NotBlank
    @Email
    private String email;
}

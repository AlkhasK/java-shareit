package ru.practicum.shareit.user.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
public class UserDto {
    @ToString.Exclude
    private Long id;
    @NotBlank
    @Size(max = 30)
    private String name;
    @NotBlank
    @Email
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDto)) return false;

        UserDto userDto = (UserDto) o;

        if (getName() != null ? !getName().equals(userDto.getName()) : userDto.getName() != null) return false;
        return getEmail() != null ? getEmail().equals(userDto.getEmail()) : userDto.getEmail() == null;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
        return result;
    }
}

package ru.practicum.shareit.user.model.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    @Test
    void toUserDtoWhenUserValidThenReturnUserDto() {
        long userId = 0L;
        String name = "name";
        String email = "email";
        User user = new User();
        user.setId(userId);
        user.setName(name);
        user.setEmail(email);

        UserDto result = UserMapper.toUserDto(user);

        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getEmail()).isEqualTo(email);
    }

    @Test
    void toUserWhenUserDtoValidThenReturnUser() {
        long userId = 0L;
        String name = "name";
        String email = "email";
        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setName(name);
        userDto.setEmail(email);

        User result = UserMapper.toUser(userDto);

        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getEmail()).isEqualTo(email);
    }

    @Test
    void testToUserWhenUserPatchDtoValidThenReturnUser() {
        String name = "name";
        String email = "email";
        UserPatchDto userPatchDto = new UserPatchDto();
        userPatchDto.setName(name);
        userPatchDto.setEmail(email);

        User result = UserMapper.toUser(userPatchDto);

        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getEmail()).isEqualTo(email);
    }
}
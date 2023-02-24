package ru.practicum.shareit.user.model.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserPatchDtoTest {

    @Autowired
    private JacksonTester<UserPatchDto> json;

    @SneakyThrows
    @Test
    void testUserPatchDto() {
        String name = "name";
        String email = "email";
        UserPatchDto userPatchDto = new UserPatchDto();
        userPatchDto.setName(name);
        userPatchDto.setEmail(email);

        JsonContent<UserPatchDto> result = json.write(userPatchDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(name);
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(email);
    }

}
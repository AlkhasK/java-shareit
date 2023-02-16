package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.model.dto.UserMapper;
import ru.practicum.shareit.user.model.dto.UserPatchDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    private final long userId = 0L;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    private UserDto userDto;

    private User user;

    @BeforeEach
    void initUserDto() {
        userDto = new UserDto();
        userDto.setId(userId);
        userDto.setName("name");
        userDto.setEmail("some@mail.ru");
    }

    @BeforeEach
    void initUser() {
        user = new User();
        user.setId(userId);
        user.setName("name");
        user.setEmail("some@mail.ru");
    }

    @SneakyThrows
    @Test
    void findAllWhenUserExistThenReturnUserDto() {
        Mockito.when(userService.getUsers())
                .thenReturn(List.of(user));
        List<UserDto> expectedResult = List.of(UserMapper.toUserDto(user));

        String result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(objectMapper.writeValueAsString(expectedResult));
    }

    @SneakyThrows
    @Test
    void findWhenUserExistThenReturnUserDto() {
        Mockito.when(userService.getUser(Mockito.anyLong()))
                .thenReturn(user);
        UserDto expectedResult = UserMapper.toUserDto(user);

        String result = mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(objectMapper.writeValueAsString(expectedResult));
    }

    @SneakyThrows
    @Test
    void createWhenUserDtoValidThenReturnUserDto() {
        Mockito.when(userService.createUser(Mockito.any()))
                .thenReturn(user);
        UserDto expectedResult = UserMapper.toUserDto(user);

        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(objectMapper.writeValueAsString(expectedResult));
    }

    @SneakyThrows
    @Test
    void updateWhenUserPatchDtoValidThenReturnUserDto() {
        Mockito.when(userService.updateUser(Mockito.anyLong(), Mockito.any()))
                .thenReturn(user);
        UserPatchDto userPatchDto = new UserPatchDto();
        userPatchDto.setName("name");
        userPatchDto.setEmail("some@mail.ru");
        UserDto expectedResult = UserMapper.toUserDto(user);

        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userPatchDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(objectMapper.writeValueAsString(expectedResult));
    }

    @SneakyThrows
    @Test
    void deleteWhenUserExistThenOk() {
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

    }
}
package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.model.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.JsonMergePatchUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> findAll() {
        return userService.getUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    public UserDto find(@PathVariable long userId) {
        User user = userService.getUser(userId);
        return UserMapper.toUserDto(user);
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("POST : create user {}", userDto);
        User user = UserMapper.toUser(userDto);
        user = userService.createUser(user);
        return UserMapper.toUserDto(user);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId, @RequestBody JsonNode userDtoPatch) {
        log.info("PATCH : update user id : {} body : {}", userId, userDtoPatch);
        User user = userService.getUser(userId);
        UserDto userDto = UserMapper.toUserDto(user);
        UserDto patchedUserDto = JsonMergePatchUtils.mergePatch(userDto, userDtoPatch, UserDto.class);
        User patchedUser = UserMapper.toUser(patchedUserDto);
        patchedUser = userService.updateUser(patchedUser);
        return UserMapper.toUserDto(patchedUser);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("DELETE : remove user id : {}", userId);
        userService.deleteUser(userId);
    }
}

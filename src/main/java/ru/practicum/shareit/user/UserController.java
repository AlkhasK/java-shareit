package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.model.dto.UserMapper;
import ru.practicum.shareit.user.model.dto.UserPatchDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
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
        log.info("GET : get all users");
        return userService.getUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    public UserDto find(@PathVariable long userId) {
        log.info("GET : get user id : {}", userId);
        User user = userService.getUser(userId);
        return UserMapper.toUserDto(user);
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("POST : create user {}", userDto);
        User user = UserMapper.toUser(userDto);
        user = userService.createUser(user);
        return UserMapper.toUserDto(user);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId, @Valid @RequestBody UserPatchDto userPatchDto) {
        log.info("PATCH : update user id : {} body : {}", userId, userPatchDto);
        User userPatch = UserMapper.toUser(userPatchDto);
        User patchedUser = userService.updateUser(userId, userPatch);
        return UserMapper.toUserDto(patchedUser);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("DELETE : remove user id : {}", userId);
        userService.deleteUser(userId);
    }
}

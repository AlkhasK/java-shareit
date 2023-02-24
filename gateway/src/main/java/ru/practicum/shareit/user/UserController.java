package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("GET : get all users");
        return userClient.getUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> find(@NotNull @Positive @PathVariable Long userId) {
        log.info("GET : get user id : {}", userId);
        return userClient.getUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        log.info("POST : create user {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@NotNull @Positive @PathVariable Long userId,
                                         @Valid @RequestBody UserPatchDto userPatchDto) {
        log.info("PATCH : update user id : {} body : {}", userId, userPatchDto);
        return userClient.updateUser(userId, userPatchDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@NotNull @Positive @PathVariable Long userId) {
        log.info("DELETE : remove user id : {}", userId);
        return userClient.deleteUser(userId);
    }
}

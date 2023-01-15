package ru.practicum.shareit.user.service;

import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

@Validated
public interface UserService {

    List<User> getUsers();

    User getUser(long id);

    User createUser(@Valid User user);

    User updateUser(@Valid User user);

    void deleteUser(long id);

}

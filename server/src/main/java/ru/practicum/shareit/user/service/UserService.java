package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    List<User> getUsers();

    User getUser(long id);

    User createUser(User user);

    User updateUser(User user);

    User updateUser(long id, User patch);

    void deleteUser(long id);

}

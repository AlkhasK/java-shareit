package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.utils.JsonMergePatchUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public List<User> getUsers() {
        return userStorage.findAll();
    }

    @Override
    public User getUser(long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No user with id : " + id));
    }

    @Override
    public User createUser(User user) {
        return userStorage.create(user);
    }

    @Override
    public User updateUser(User user) {
        return userStorage.update(user);
    }

    @Override
    public User updateUser(long id, User patch) {
        User user = getUser(id);
        User patchedUser = JsonMergePatchUtils.mergePatch(user, patch, User.class);
        return updateUser(patchedUser);
    }

    @Override
    public void deleteUser(long id) {
        userStorage.delete(id);
    }
}

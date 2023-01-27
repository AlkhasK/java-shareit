package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.exceptions.EntityNotFoundException;
import ru.practicum.shareit.error.exceptions.UniqueConstraintException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    private long id = 0;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public User create(User user) {
        boolean isEmailNotUnique = users.values().stream()
                .map(User::getEmail)
                .anyMatch(email -> email.equals(user.getEmail()));
        if (isEmailNotUnique) {
            throw new UniqueConstraintException(String.format("Email %s isn't unique", user.getEmail()));
        }
        user.setId(getId());
        users.put(user.getId(), user);
        return user;
    }

    private long getId() {
        return ++id;
    }

    @Override
    public User update(User user) {
        boolean isEmailNotUnique = users.values().stream()
                .filter(u -> !u.getId().equals(user.getId()))
                .map(User::getEmail)
                .anyMatch(email -> email.equals(user.getEmail()));
        if (isEmailNotUnique) {
            throw new UniqueConstraintException(String.format("Email %s isn't unique", user.getEmail()));
        }
        User oldUser = users.replace(user.getId(), user);
        if (oldUser == null) {
            throw new EntityNotFoundException("No user with id : " + user.getId());
        }
        return user;
    }

    @Override
    public void delete(long userId) {
        User deletedUser = users.remove(userId);
        if (deletedUser == null) {
            throw new EntityNotFoundException("No user with id : " + userId);
        }
    }
}

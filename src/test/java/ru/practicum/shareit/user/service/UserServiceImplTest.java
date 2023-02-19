package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void initUser() {
        user = new User();
        user.setId(0L);
        user.setName("name");
        user.setEmail("email");
    }

    @Test
    void getUserWhenUserExistThenReturnUser() {
        long userId = 0L;
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        User result = userService.getUser(userId);

        assertThat(result).isEqualTo(user);
    }

    @Test
    void getUserWhenUserNotExistThenInterrupt() {
        long userId = 0L;
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUser(userId));
    }

    @Test
    void getUsersWhenUserExistThenReturnUser() {
        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(user));

        List<User> result = userService.getUsers();

        assertThat(result).hasSize(1);
    }

    @Test
    void createUserWhenUserValidThenReturnUser() {
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(user);

        User result = userService.createUser(user);

        assertThat(result).isEqualTo(user);
    }

    @Test
    void updateUserWhenUserValidThenReturnUser() {
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(user);

        User result = userService.updateUser(user);

        assertThat(result).isEqualTo(user);
    }

    @Test
    void updateUserWhenUserExistAndPatchValidThenReturnUser() {
        long userId = 0L;
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(user);

        User result = userService.updateUser(userId, user);

        assertThat(result).isEqualTo(user);
    }

    @Test
    void deleteUserWhenUserExistsThenOk() {
        long userId = 0L;

        userService.deleteUser(userId);
    }
}
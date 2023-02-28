package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    private Item item;
    private User user;
    private ItemRequest itemRequest;
    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void initUser() {
        String userName = "name";
        String userEmail = "email";
        user = new User();
        user.setName(userName);
        user.setEmail(userEmail);
    }

    @BeforeEach
    void initItem() {
        String name = "name";
        String description = "description";
        boolean available = true;
        item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
    }

    @BeforeEach
    void initItemRequest() {
        itemRequest = new ItemRequest();
        itemRequest.setDescription("text");
        itemRequest.setCreated(LocalDateTime.now());
    }

    @Test
    void findByOwner_IdWhenItemWithOwnerExistsThenReturnItem() {
        User createdUser = entityManager.persistAndFlush(user);
        item.setOwner(createdUser);
        entityManager.persistAndFlush(item);

        Page<Item> result = itemRepository.findByOwner_Id(createdUser.getId(), PageRequest.of(0, 5));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void searchWhenItemWithTextExistTHenReturnItem() {
        String text = "DesCr";
        User createdUser = entityManager.persistAndFlush(user);
        item.setOwner(createdUser);
        entityManager.persistAndFlush(item);

        Page<Item> result = itemRepository.search(text, PageRequest.of(0, 5));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void findByRequest_IdInWhenItemWithRequestExistsThenReturnItem() {
        User createdUser = entityManager.persistAndFlush(user);
        itemRequest.setRequestor(createdUser);
        ItemRequest createdItemRequest = entityManager.persistAndFlush(itemRequest);
        item.setOwner(createdUser);
        item.setRequest(createdItemRequest);
        entityManager.persistAndFlush(item);

        List<Item> result = itemRepository.findByRequest_IdIn(List.of(createdItemRequest.getId()));

        assertThat(result).hasSize(1);
    }

    @Test
    void findByRequest_IdWhenItemWithRequestExistsThenReturnItem() {
        User createdUser = entityManager.persistAndFlush(user);
        itemRequest.setRequestor(createdUser);
        ItemRequest createdItemRequest = entityManager.persistAndFlush(itemRequest);
        item.setOwner(createdUser);
        item.setRequest(createdItemRequest);
        entityManager.persistAndFlush(item);

        List<Item> result = itemRepository.findByRequest_IdIn(List.of(createdItemRequest.getId()));

        assertThat(result).hasSize(1);
    }
}
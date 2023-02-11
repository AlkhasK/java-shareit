package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    private Item item;
    private User user;
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
}
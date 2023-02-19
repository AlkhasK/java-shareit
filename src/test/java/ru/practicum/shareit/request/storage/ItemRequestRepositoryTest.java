package ru.practicum.shareit.request.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    private ItemRequest itemRequest;
    private User user;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void initUser() {
        String userName = "name";
        String userEmail = "email";
        user = new User();
        user.setName(userName);
        user.setEmail(userEmail);
    }

    @BeforeEach
    void initItemRequest() {
        String itemRequestDescription = "description";
        LocalDateTime itemRequestUserCreated = LocalDateTime.now();
        itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDescription);
        itemRequest.setCreated(itemRequestUserCreated);
    }

    @Test
    void saveWhenItemRequestThenReturnItemRequest() {
        User createdUser = entityManager.persistAndFlush(user);
        itemRequest.setRequestor(createdUser);
        ItemRequest itemRequestSaved = itemRequestRepository.save(itemRequest);
        TypedQuery<ItemRequest> query = entityManager.getEntityManager()
                .createQuery("select i from ItemRequest i where i.id = ?1",
                        ItemRequest.class);
        ItemRequest itemRequestExtract = query.setParameter(1, itemRequestSaved.getId()).getSingleResult();

        assertThat(itemRequestSaved.getId()).isEqualTo(itemRequestExtract.getId());
        assertThat(itemRequestSaved.getDescription()).isEqualTo(itemRequestExtract.getDescription());
        assertThat(itemRequestSaved.getCreated()).isEqualTo(itemRequestExtract.getCreated());
    }

    @Test
    void findByIdWhenIdExistThenReturnItemRequest() {
        User createdUser = entityManager.persistAndFlush(user);
        itemRequest.setRequestor(createdUser);
        ItemRequest createdItemRequest = entityManager.persistAndFlush(itemRequest);

        Optional<ItemRequest> itemRequestExtract = itemRequestRepository.findById(createdItemRequest.getId());

        assertThat(itemRequestExtract).isPresent();
    }

    @Test
    void findByRequestor_IdWhenIdExistThenReturnItemRequest() {
        User createdUser = entityManager.persistAndFlush(user);
        itemRequest.setRequestor(createdUser);
        entityManager.persistAndFlush(itemRequest);

        List<ItemRequest> itemRequestExtract = itemRequestRepository.findByRequestor_Id(createdUser.getId(),
                Sort.unsorted());

        assertThat(itemRequestExtract).hasSize(1);
    }

    @Test
    void findByRequestor_IdNotWhenIdExistThenReturnItemRequest() {
        long userIdNotExist = 99;
        User createdUser = entityManager.persistAndFlush(user);
        itemRequest.setRequestor(createdUser);
        entityManager.persistAndFlush(itemRequest);

        Page<ItemRequest> itemRequestExtract = itemRequestRepository.findByRequestor_IdNot(userIdNotExist,
                PageRequest.of(0, 5));

        assertThat(itemRequestExtract.getContent()).hasSize(1);
    }
}
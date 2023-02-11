package ru.practicum.shareit.item.comment.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    private Comment comment;
    private Item item;
    private User user;
    @Autowired
    private CommentRepository commentRepository;

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
    void initComment() {
        comment = new Comment();
        comment.setText("text");
        comment.setCreated(LocalDateTime.now());
    }

    @Test
    void findAllByItem_IdWhenCommentExistThenReturnComment() {
        User createdUser = entityManager.persistAndFlush(user);
        item.setOwner(createdUser);
        Item createdItem = entityManager.persistAndFlush(item);
        comment.setItem(createdItem);
        comment.setAuthor(createdUser);
        entityManager.persistAndFlush(comment);

        List<Comment> result = commentRepository.findAllByItem_Id(createdItem.getId());

        assertThat(result).hasSize(1);
    }

    @Test
    void findAllByItem_IdInWhenCommentExistThenReturnComment() {
        User createdUser = entityManager.persistAndFlush(user);
        item.setOwner(createdUser);
        Item createdItem = entityManager.persistAndFlush(item);
        comment.setItem(createdItem);
        comment.setAuthor(createdUser);
        entityManager.persistAndFlush(comment);

        List<Comment> result = commentRepository.findAllByItem_IdIn(List.of(createdItem.getId()));

        assertThat(result).hasSize(1);
    }
}
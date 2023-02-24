package ru.practicum.shareit.item.comment.model.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CommentMapperTest {

    private final CommentMapper commentMapper = new CommentMapper();

    @Test
    void toCommentWhenCommentAndItemAndUserValid() {
        String text = "text";
        int timeCorrection = 1;
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText(text);
        Item item = new Item();
        User user = new User();

        Comment result = commentMapper.toComment(commentCreateDto, user, item);

        assertThat(result.getText()).isEqualTo(text);
        assertThat(result.getAuthor()).isEqualTo(user);
        assertThat(result.getItem()).isEqualTo(item);
        assertThat(result.getCreated()).isBefore(LocalDateTime.now().plusMinutes(timeCorrection));
    }

    @Test
    void toCommentDtoWhenCommentValidThenReturnCommentDto() {
        String text = "text";
        String name = "name";
        LocalDateTime created = LocalDateTime.now();
        User user = new User();
        user.setName(name);
        Comment comment = new Comment();
        comment.setAuthor(user);
        comment.setCreated(created);
        comment.setText(text);

        CommentDto result = commentMapper.toCommentDto(comment);

        assertThat(result.getText()).isEqualTo(text);
        assertThat(result.getAuthorName()).isEqualTo(name);
        assertThat(result.getCreated()).isEqualTo(created);
    }
}
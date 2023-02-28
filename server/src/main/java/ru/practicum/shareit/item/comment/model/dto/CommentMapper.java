package ru.practicum.shareit.item.comment.model.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Component
public class CommentMapper {

    public Comment toComment(CommentCreateDto commentCreateDto, User author, Item item) {
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setText(commentCreateDto.getText());
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public CommentDto toCommentDto(Comment comment) {
        User author = comment.getAuthor();
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setAuthorName(author.getName());
        commentDto.setText(comment.getText());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

}

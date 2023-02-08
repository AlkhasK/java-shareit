package ru.practicum.shareit.item.comment.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class CommentDto {
    private Long id;
    @NotBlank
    @Size(max = 1025)
    private String text;
    private String authorName;
    @PastOrPresent
    private LocalDateTime created;
}

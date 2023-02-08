package ru.practicum.shareit.item.comment.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
public class CommentCreateDto {
    @NotBlank
    @Size(max = 1025)
    private String text;
}

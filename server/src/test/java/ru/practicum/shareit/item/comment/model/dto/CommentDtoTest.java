package ru.practicum.shareit.item.comment.model.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @SneakyThrows
    @Test
    void testCommentDto() {
        long commentDtoId = 0;
        String commentDtoText = "text";
        String commentDtoAuthorName = "name";
        LocalDateTime created = LocalDateTime.now();
        CommentDto commentDto = new CommentDto();
        commentDto.setId(commentDtoId);
        commentDto.setText(commentDtoText);
        commentDto.setCreated(created);
        commentDto.setAuthorName(commentDtoAuthorName);

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(0);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(commentDtoText);
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").format(created));
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(commentDtoAuthorName);
    }
}
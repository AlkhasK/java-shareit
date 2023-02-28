package ru.practicum.shareit.request.model.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestCreateDtoTest {

    @Autowired
    private JacksonTester<ItemRequestCreateDto> json;

    @SneakyThrows
    @Test
    void testItemRequestCreateDto() {
        String description = "some description";
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto();
        itemRequestCreateDto.setDescription(description);

        JsonContent<ItemRequestCreateDto> result = json.write(itemRequestCreateDto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(description);
    }
}
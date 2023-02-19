package ru.practicum.shareit.item.model.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @SneakyThrows
    @Test
    void testItemDto() {
        ItemDto itemDto = new ItemDto();
        long itemId = 1;
        String itemName = "name";
        String itemDescription = "item description";
        boolean available = true;
        long requestId = 0;
        itemDto.setId(itemId);
        itemDto.setName(itemName);
        itemDto.setDescription(itemDescription);
        itemDto.setAvailable(available);
        itemDto.setRequestId(requestId);

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemName);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDescription);
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(0);
    }

}
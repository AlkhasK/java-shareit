package ru.practicum.shareit.item.model.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemPatchDtoTest {

    @Autowired
    private JacksonTester<ItemPatchDto> json;

    @SneakyThrows
    @Test
    void testItemPatchDto() {
        ItemPatchDto itemPatchDto = new ItemPatchDto();
        String itemName = "name";
        String itemDescription = "item description";
        boolean available = true;
        itemPatchDto.setName(itemName);
        itemPatchDto.setDescription(itemDescription);
        itemPatchDto.setAvailable(available);

        JsonContent<ItemPatchDto> result = json.write(itemPatchDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemName);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDescription);
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }

}
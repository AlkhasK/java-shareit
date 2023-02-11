package ru.practicum.shareit.request.model.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @SneakyThrows
    @Test
    void testItemRequestDto() {
        long itemRequestId = 0;
        String itemRequestDescription = "description";
        LocalDateTime created = LocalDateTime.now();
        long itemId = 1;
        String itemName = "name";
        long ownerId = 2;
        String itemDescription = "item description";
        boolean available = true;
        long requestId = 0;
        ItemRequestDto.ItemDto itemDto = new ItemRequestDto.ItemDto();
        itemDto.setId(itemId);
        itemDto.setName(itemName);
        itemDto.setDescription(itemDescription);
        itemDto.setOwnerId(ownerId);
        itemDto.setAvailable(available);
        itemDto.setRequestId(requestId);
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequestId);
        itemRequestDto.setDescription(itemRequestDescription);
        itemRequestDto.setCreated(created);
        itemRequestDto.setItems(List.of(itemDto));

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(0);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemRequestDescription);
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").format(created));
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo(itemName);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].ownerId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.items[0].description").isEqualTo(itemDescription);
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(0);
    }

}
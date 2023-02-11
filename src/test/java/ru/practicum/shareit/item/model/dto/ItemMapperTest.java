package ru.practicum.shareit.item.model.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    @Test
    void toItemDtoWhenValidItemReturnItemDto() {
        long itemId = 0L;
        long itemRequestId = 1L;
        String name = "name";
        String description = "description";
        boolean available = true;
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestId);
        Item item = new Item();
        item.setId(itemId);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setRequest(itemRequest);

        ItemDto result = ItemMapper.toItemDto(item);

        assertThat(result.getId()).isEqualTo(itemId);
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getDescription()).isEqualTo(description);
        assertThat(result.getAvailable()).isEqualTo(available);
        assertThat(result.getRequestId()).isEqualTo(itemRequestId);
    }

    @Test
    void toItemWhenValidItemDtoThenReturnItem() {
        long itemId = 0L;
        String name = "name";
        String description = "description";
        boolean available = true;
        User user = new User();
        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);

        Item result = ItemMapper.toItem(itemDto, user);

        assertThat(result.getId()).isEqualTo(itemId);
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getDescription()).isEqualTo(description);
        assertThat(result.getAvailable()).isEqualTo(available);
        assertThat(result.getOwner()).isEqualTo(user);
    }

    @Test
    void toItemWhenValidItemDtoRequestItemUserThenReturnItem() {
        long itemId = 0L;
        String name = "name";
        String description = "description";
        boolean available = true;
        User user = new User();
        ItemRequest itemRequest = new ItemRequest();
        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);

        Item result = ItemMapper.toItem(itemDto, itemRequest, user);

        assertThat(result.getId()).isEqualTo(itemId);
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getDescription()).isEqualTo(description);
        assertThat(result.getAvailable()).isEqualTo(available);
        assertThat(result.getOwner()).isEqualTo(user);
        assertThat(result.getRequest()).isEqualTo(itemRequest);
    }

    @Test
    void toItemWhenPatchAndUserValidThenReturnItem() {
        String name = "name";
        String description = "description";
        boolean available = true;
        User user = new User();
        ItemPatchDto itemPatchDto = new ItemPatchDto();
        itemPatchDto.setName(name);
        itemPatchDto.setDescription(description);
        itemPatchDto.setAvailable(available);

        Item result = ItemMapper.toItem(itemPatchDto, user);

        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getDescription()).isEqualTo(description);
        assertThat(result.getAvailable()).isEqualTo(available);
        assertThat(result.getOwner()).isEqualTo(user);
    }
}
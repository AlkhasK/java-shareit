package ru.practicum.shareit.item.model.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        ItemRequest itemRequest = item.getRequest();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(itemRequest != null ? itemRequest.getId() : null);
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);
        return item;
    }

    public static Item toItem(ItemDto itemDto, ItemRequest itemRequest, User owner) {
        Item item = toItem(itemDto, owner);
        item.setRequest(itemRequest);
        return item;
    }

    public static Item toItem(ItemPatchDto itemPatchDto, User owner) {
        Item item = new Item();
        item.setName(itemPatchDto.getName());
        item.setDescription(itemPatchDto.getDescription());
        item.setAvailable(itemPatchDto.getAvailable());
        item.setOwner(owner);
        return item;
    }
}

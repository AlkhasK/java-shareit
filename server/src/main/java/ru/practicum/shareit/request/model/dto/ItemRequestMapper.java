package ru.practicum.shareit.request.model.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ItemRequestMapper {

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());
        itemRequestDto.setItems(Collections.emptyList());
        return itemRequestDto;
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<Item> items) {
        ItemRequestDto itemRequestDto = toItemRequestDto(itemRequest);
        if (Objects.nonNull(items)) {
            List<ItemRequestDto.ItemDto> itemDtos = items.stream()
                    .map(this::toItemDto)
                    .collect(Collectors.toList());
            itemRequestDto.setItems(itemDtos);
        } else {
            itemRequestDto.setItems(Collections.emptyList());
        }
        return itemRequestDto;
    }

    private ItemRequestDto.ItemDto toItemDto(Item item) {
        ItemRequestDto.ItemDto itemDto = new ItemRequestDto.ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setOwnerId(item.getOwner().getId());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(item.getRequest().getId());
        return itemDto;
    }

    public ItemRequest toItemRequest(User requestor, ItemRequestCreateDto itemRequestCreateDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestCreateDto.getDescription());
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }
}

package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.exceptions.PermissionException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utils.JsonMergePatchUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader(USER_ID_HEADER) long userId,
                          @RequestBody ItemDto itemDto) {
        log.info("POST : create item {}", itemDto);
        Item item = ItemMapper.toItem(userId, itemDto);
        item = itemService.createItem(item);
        return ItemMapper.toItemDto(item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable long itemId,
                          @RequestBody JsonNode itemDtoPatch) {
        log.info("PATCH : update item id : {} body : {}", itemId, itemDtoPatch);
        Item item = itemService.getItem(itemId);
        if (item.getOwnerId() != userId) {
            throw new PermissionException("User doesn't have permission for updating item id :" + item.getId());
        }
        ItemDto itemDto = ItemMapper.toItemDto(item);
        ItemDto patchedItemDto = JsonMergePatchUtils.mergePatch(itemDto, itemDtoPatch, ItemDto.class);
        Item patchedItem = ItemMapper.toItem(userId, patchedItemDto);
        patchedItem = itemService.updateItem(patchedItem);
        return ItemMapper.toItemDto(patchedItem);
    }

    @GetMapping("/{itemId}")
    public ItemDto find(@PathVariable long itemId) {
        Item item = itemService.getItem(itemId);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping
    public List<ItemDto> findAllForUser(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.getItemsForUser(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.searchItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}

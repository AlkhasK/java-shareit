package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemMapper;
import ru.practicum.shareit.item.model.dto.ItemPatchDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    private final UserService userService;

    @PostMapping
    public ItemDto create(@RequestHeader(USER_ID_HEADER) long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.info("POST : create item {}", itemDto);
        User owner = userService.getUser(userId);
        Item item = ItemMapper.toItem(itemDto, owner);
        item = itemService.createItem(item);
        return ItemMapper.toItemDto(item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable long itemId,
                          @Valid @RequestBody ItemPatchDto itemPatchDto) {
        log.info("PATCH : update item id : {} body : {}", itemId, itemPatchDto);
        User owner = userService.getUser(userId);
        Item itemPatch = ItemMapper.toItem(itemPatchDto, owner);
        Item patchedItem = itemService.updateItem(itemId, itemPatch);
        return ItemMapper.toItemDto(patchedItem);
    }

    @GetMapping("/{itemId}")
    public ItemDto find(@PathVariable long itemId) {
        log.info("GET : get item id : {}", itemId);
        Item item = itemService.getItem(itemId);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping
    public List<ItemDto> findAllForUser(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("GET : get items for user id : {}", userId);
        return itemService.getItemsForUser(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.info("GET : search items by text : {}", text);
        return itemService.searchItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}

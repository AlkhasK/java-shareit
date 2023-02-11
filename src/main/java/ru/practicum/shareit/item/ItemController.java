package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.model.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemPatchDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utils.ControllerConstants;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader(ControllerConstants.USER_ID_HEADER) long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.info("POST : create item {}", itemDto);
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(ControllerConstants.USER_ID_HEADER) long userId, @PathVariable long itemId,
                          @Valid @RequestBody ItemPatchDto itemPatchDto) {
        log.info("PATCH : update item id : {} body : {}", itemId, itemPatchDto);
        return itemService.updateItem(userId, itemId, itemPatchDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto find(@RequestHeader(ControllerConstants.USER_ID_HEADER) long userId,
                        @PathVariable long itemId) {
        log.info("GET : get item id : {}", itemId);
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> findAllForUser(@RequestHeader(ControllerConstants.USER_ID_HEADER) long userId,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "5") int size) {
        log.info("GET : get items for user id : {}", userId);
        return itemService.getItemsForUser(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text,
                                @RequestParam(defaultValue = "0") int from,
                                @RequestParam(defaultValue = "5") int size) {
        log.info("GET : search items by text : {}", text);
        return itemService.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(ControllerConstants.USER_ID_HEADER) long userId,
                                 @PathVariable long itemId, @Valid @RequestBody CommentCreateDto commentCreateDto) {
        log.info("POST : add comment : {} to item : {}", commentCreateDto, itemId);
        return itemService.addComment(userId, itemId, commentCreateDto);
    }
}

package ru.practicum.shareit.Item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Item.comment.CommentClient;
import ru.practicum.shareit.Item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.Item.dto.ItemDto;
import ru.practicum.shareit.Item.dto.ItemPatchDto;
import ru.practicum.shareit.constant.Constants;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    private final CommentClient commentClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(Constants.USER_ID_HEADER) long userId,
                                         @Valid @RequestBody ItemDto itemDto) {
        log.info("POST : create item {}", itemDto);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(Constants.USER_ID_HEADER) long userId,
                                         @NotNull @Positive @PathVariable Long itemId,
                                         @Valid @RequestBody ItemPatchDto itemPatchDto) {
        log.info("PATCH : update item id : {} body : {}", itemId, itemPatchDto);
        return itemClient.updateItem(userId, itemId, itemPatchDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> find(@RequestHeader(Constants.USER_ID_HEADER) long userId,
                                       @NotNull @PathVariable Long itemId) {
        log.info("GET : get item id : {}", itemId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllForUser(@RequestHeader(Constants.USER_ID_HEADER) long userId,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "5") Integer size) {
        log.info("GET : get items for user id : {}", userId);
        return itemClient.getItemsForUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "5") Integer size) {
        log.info("GET : search items by text : {}", text);
        return itemClient.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(Constants.USER_ID_HEADER) long userId,
                                             @NotNull @Positive @PathVariable Long itemId,
                                             @Valid @RequestBody CommentCreateDto commentCreateDto) {
        log.info("POST : add comment : {} to item : {}", commentCreateDto, itemId);
        return commentClient.addComment(userId, itemId, commentCreateDto);
    }
}

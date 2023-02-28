package ru.practicum.shareit.request;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(Constants.USER_ID_HEADER) long userId,
                                         @Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        log.info("POST : user : {} create item request : {}", userId, itemRequestCreateDto);
        return itemRequestClient.createItemRequest(userId, itemRequestCreateDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllCreatedByUser(@RequestHeader(Constants.USER_ID_HEADER) long userId) {
        log.info("GET : find all item requests created by user : {}", userId);
        return itemRequestClient.getAllItemRequestCreatedByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllCreatedByOther(@RequestHeader(Constants.USER_ID_HEADER) long userId,
                                                        @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(defaultValue = "5") Integer size) {
        log.info("GET : user : {} find all item requests", userId);
        return itemRequestClient.findAllCreatedByOther(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> find(@RequestHeader(Constants.USER_ID_HEADER) long userId,
                                       @NonNull @Positive @PathVariable Long requestId) {
        log.info("GET : find item requests id : {}", requestId);
        return itemRequestClient.getItemRequest(userId, requestId);
    }

}

package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.utils.ControllerConstants;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(ControllerConstants.USER_ID_HEADER) long userId,
                                 @Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        log.info("POST : user : {} create item request : {}", userId, itemRequestCreateDto);
        return itemRequestService.createItemRequest(userId, itemRequestCreateDto);
    }

    @GetMapping
    public List<ItemRequestDto> findAllCreatedByUser(@RequestHeader(ControllerConstants.USER_ID_HEADER) long userId) {
        log.info("GET : find all item requests created by user : {}", userId);
        return itemRequestService.getAllItemRequestCreatedByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllCreatedByOther(@RequestHeader(ControllerConstants.USER_ID_HEADER) long userId,
                                                      @RequestParam(defaultValue = "0") int from,
                                                      @RequestParam(defaultValue = "5") int size) {
        log.info("GET : user : {} find all item requests", userId);
        return itemRequestService.findAllCreatedByOther(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto find(@RequestHeader(ControllerConstants.USER_ID_HEADER) long userId,
                               @PathVariable long requestId) {
        log.info("GET : find item requests id : {}", requestId);
        return itemRequestService.getItemRequest(userId, requestId);
    }

}

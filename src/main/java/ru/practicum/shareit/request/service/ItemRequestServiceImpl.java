package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.dto.ItemRequestMapper;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.pagination.PageRequestWithOffset;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private static final Sort SORT_CREATED_DESC = Sort.by(Sort.Direction.DESC, "created");

    private final ItemRequestRepository itemRequestRepository;

    private final UserService userService;

    private final ItemRequestMapper itemRequestMapper;

    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(long userId, ItemRequestCreateDto itemRequestCreateDto) {
        User requestor = userService.getUser(userId);
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(requestor, itemRequestCreateDto);
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestDto(savedItemRequest);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequestCreatedByUser(long userId) {
        userService.getUser(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestor_Id(userId, SORT_CREATED_DESC);
        if (itemRequests.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> itemRequestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        Map<Long, List<Item>> itemsGroupedByItemRequests = getItems(itemRequestIds);
        return itemRequests.stream()
                .map((ItemRequest itmReq) -> itemRequestMapper.toItemRequestDto(itmReq,
                        itemsGroupedByItemRequests.get(itmReq.getId())))
                .collect(Collectors.toList());
    }

    private Map<Long, List<Item>> getItems(List<Long> itemRequestIds) {
        List<Item> items = itemRepository.findByRequest_IdIn(itemRequestIds);
        return items.stream()
                .collect(Collectors.groupingBy((Item itm) -> itm.getRequest().getId()));
    }

    @Override
    public List<ItemRequestDto> findAllCreatedByOther(long userId, int from, int size) {
        userService.getUser(userId);
        Pageable pageable = PageRequestWithOffset.of(from, size, SORT_CREATED_DESC);
        Page<ItemRequest> pageItemRequests = itemRequestRepository.findByRequestor_IdNot(userId, pageable);
        if (pageItemRequests.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> itemRequestIds = pageItemRequests.getContent().stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        Map<Long, List<Item>> itemsGroupedByItemRequests = getItems(itemRequestIds);
        return pageItemRequests.getContent().stream()
                .map((ItemRequest itmReq) -> itemRequestMapper.toItemRequestDto(itmReq,
                        itemsGroupedByItemRequests.get(itmReq.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequest(long userId, long requestId) {
        userService.getUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("No item request id : " + requestId));
        List<Item> items = itemRepository.findByRequest_Id(requestId);
        return itemRequestMapper.toItemRequestDto(itemRequest, items);
    }

}

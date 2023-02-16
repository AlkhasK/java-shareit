package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import ru.practicum.shareit.utils.PageUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
        if (!itemRequests.iterator().hasNext()) {
            return Collections.emptyList();
        }
        List<Long> itemRequestIds = iteratorToStream(itemRequests)
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        Map<Long, List<Item>> itemsGroupedByItemRequests = getItems(itemRequestIds);
        return iteratorToStream(itemRequests)
                .map((ItemRequest itmReq) -> itemRequestMapper.toItemRequestDto(itmReq,
                        itemsGroupedByItemRequests.get(itmReq.getId())))
                .collect(Collectors.toList());
    }

    private Map<Long, List<Item>> getItems(List<Long> itemRequestIds) {
        Iterable<Item> items = itemRepository.findByRequest_IdIn(itemRequestIds);
        return iteratorToStream(items)
                .collect(Collectors.groupingBy((Item itm) -> itm.getRequest().getId()));
    }

    private <T> Stream<T> iteratorToStream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    @Override
    public List<ItemRequestDto> findAllCreatedByOther(long userId, int from, int size) {
        userService.getUser(userId);
        Map<String, Integer> pageableParam = PageUtils.getPageableParam(from, size);
        Pageable pageable = PageRequest.of(pageableParam.get("page"), pageableParam.get("size"), SORT_CREATED_DESC);
        Page<ItemRequest> pageItemRequests = itemRequestRepository.findByRequestor_IdNot(userId, pageable);
        List<ItemRequest> itemRequests = PageUtils.getElements(pageItemRequests.getContent(), from, size);
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

    @Override
    public ItemRequestDto getItemRequest(long userId, long requestId) {
        userService.getUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("No item request id : " + requestId));
        Iterable<Item> items = itemRepository.findByRequest_Id(requestId);
        return itemRequestMapper.toItemRequestDto(itemRequest, iteratorToStream(items).collect(Collectors.toList()));
    }

}

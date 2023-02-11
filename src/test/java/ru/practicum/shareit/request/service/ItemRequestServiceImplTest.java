package ru.practicum.shareit.request.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void createItemRequestWhenUserValidThenItemRequestSaved() {
        long userId = 0L;
        User requestor = new User();
        ItemRequest itemRequest = new ItemRequest();
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto();
        Mockito.when(userService.getUser(userId))
                .thenReturn(requestor);
        Mockito.when(itemRequestMapper.toItemRequest(requestor, itemRequestCreateDto))
                .thenReturn(itemRequest);
        Mockito.when(itemRequestRepository.save(itemRequest))
                .thenReturn(itemRequest);
        Mockito.when(itemRequestMapper.toItemRequestDto(itemRequest))
                .thenReturn(itemRequestDto);

        ItemRequestDto itemRequestDtoReturned = itemRequestService.createItemRequest(userId, itemRequestCreateDto);

        Mockito.verify(itemRequestRepository, Mockito.times(1)).save(itemRequest);
        assertThat(itemRequestDtoReturned).isEqualTo(itemRequestDto);
    }

    @Test
    void createItemRequestWhenUserNonValidThenItemRequestNotSaved() {
        long userId = 0L;
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto();
        Mockito.when(userService.getUser(userId))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.createItemRequest(userId, itemRequestCreateDto));

        Mockito.verify(itemRequestRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void getAllItemRequestCreatedByUserWhenUserValidAndHaveRequestWithResponsesThenReturnRequestWithResponses() {
        long userId = 0L;
        long itemRequestId = 0L;
        long itemId = 0L;
        User requestor = new User();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestId);
        List<ItemRequest> itemRequestIterable = List.of(itemRequest);
        Item item = new Item();
        item.setId(itemId);
        item.setRequest(itemRequest);
        List<Item> itemIterable = List.of(item);
        ItemRequestDto.ItemDto itemDto = new ItemRequestDto.ItemDto();
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setItems(List.of(itemDto));
        Mockito.when(userService.getUser(userId))
                .thenReturn(requestor);
        Mockito.when(itemRequestRepository.findAll(Mockito.any(BooleanExpression.class), Mockito.any(Sort.class)))
                .thenReturn(itemRequestIterable);
        Mockito.when(itemRequestMapper.toItemRequestDto(itemRequest, itemIterable))
                .thenReturn(itemRequestDto);
        Mockito.when(itemRepository.findAll(Mockito.any(BooleanExpression.class)))
                .thenReturn(List.of(item));

        List<ItemRequestDto> itemRequestDtos = itemRequestService.getAllItemRequestCreatedByUser(userId);

        assertThat(itemRequestDtos).isEqualTo(List.of(itemRequestDto));
        assertThat(itemRequestDtos.stream().findFirst().get().getItems())
                .isEqualTo(List.of(itemDto));
    }

    @Test
    void getAllItemRequestCreatedByUserWhenUserValidAndHaveRequestWithoutResponsesThenReturnRequestWithoutResponses() {
        long userId = 0L;
        long itemRequestId = 0L;
        User requestor = new User();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestId);
        List<ItemRequest> itemRequestIterable = List.of(itemRequest);
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        Mockito.when(userService.getUser(userId))
                .thenReturn(requestor);
        Mockito.when(itemRequestRepository.findAll(Mockito.any(BooleanExpression.class), Mockito.any(Sort.class)))
                .thenReturn(itemRequestIterable);
        Mockito.when(itemRequestMapper.toItemRequestDto(itemRequest, null))
                .thenReturn(itemRequestDto);
        Mockito.when(itemRepository.findAll(Mockito.any(BooleanExpression.class)))
                .thenReturn(Collections.emptyList());

        List<ItemRequestDto> itemRequestDtos = itemRequestService.getAllItemRequestCreatedByUser(userId);

        assertThat(itemRequestDtos).isEqualTo(List.of(itemRequestDto));
        assertThat(itemRequestDtos.stream().findFirst().get().getItems()).isNull();
    }

    @Test
    void getAllItemRequestCreatedByUserWhenUserValidAndNotHaveRequestThenReturnEmptyList() {
        long userId = 0L;
        User requestor = new User();
        Mockito.when(userService.getUser(userId))
                .thenReturn(requestor);
        Mockito.when(itemRequestRepository.findAll(Mockito.any(BooleanExpression.class), Mockito.any(Sort.class)))
                .thenReturn(Collections.emptyList());

        List<ItemRequestDto> itemRequestDtos = itemRequestService.getAllItemRequestCreatedByUser(userId);

        assertThat(itemRequestDtos).asList().isEmpty();
        Mockito.verify(itemRequestMapper, Mockito.never())
                .toItemRequestDto(Mockito.any(), Mockito.any());
    }

    @Test
    void getAllItemRequestCreatedByUserWhenUserNotValidThenInterrupt() {
        long userId = 0L;
        Mockito.when(userService.getUser(userId))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.getAllItemRequestCreatedByUser(userId));
        Mockito.verify(itemRequestRepository, Mockito.never())
                .findAll(Mockito.any(BooleanExpression.class), Mockito.any(Sort.class));
    }

    @Test
    void findAllCreatedByOtherWhenUserValidAndRequestExistWithResponseThenReturnRequestsWithResponse() {
        long userId = 0L;
        long itemRequestId = 0L;
        long itemId = 0L;
        int from = 0;
        int size = 1;
        User requestor = new User();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestId);
        Page<ItemRequest> itemRequestPage = new PageImpl<>(List.of(itemRequest));
        Item item = new Item();
        item.setId(itemId);
        item.setRequest(itemRequest);
        List<Item> itemIterable = List.of(item);
        ItemRequestDto.ItemDto itemDto = new ItemRequestDto.ItemDto();
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setItems(List.of(itemDto));
        Mockito.when(userService.getUser(userId))
                .thenReturn(requestor);
        Mockito.when(itemRequestRepository.findAll(Mockito.any(BooleanExpression.class), Mockito.any(Pageable.class)))
                .thenReturn(itemRequestPage);
        Mockito.when(itemRequestMapper.toItemRequestDto(itemRequest, itemIterable))
                .thenReturn(itemRequestDto);
        Mockito.when(itemRepository.findAll(Mockito.any(BooleanExpression.class)))
                .thenReturn(List.of(item));

        List<ItemRequestDto> itemRequestDtos = itemRequestService
                .findAllCreatedByOther(userId, from, size);

        assertThat(itemRequestDtos).isEqualTo(List.of(itemRequestDto));
    }

    @Test
    void findAllCreatedByOtherWhenUserValidAndRequestExistWithoutResponseThenReturnRequestsWithoutResponse() {
        long userId = 0L;
        long itemRequestId = 0L;
        int from = 0;
        int size = 1;
        User requestor = new User();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestId);
        Page<ItemRequest> itemRequestPage = new PageImpl<>(List.of(itemRequest));
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        Mockito.when(userService.getUser(userId))
                .thenReturn(requestor);
        Mockito.when(itemRequestRepository.findAll(Mockito.any(BooleanExpression.class), Mockito.any(Pageable.class)))
                .thenReturn(itemRequestPage);
        Mockito.when(itemRequestMapper.toItemRequestDto(itemRequest, null))
                .thenReturn(itemRequestDto);
        Mockito.when(itemRepository.findAll(Mockito.any(BooleanExpression.class)))
                .thenReturn(Collections.emptyList());

        List<ItemRequestDto> itemRequestDtos = itemRequestService
                .findAllCreatedByOther(userId, from, size);

        assertThat(itemRequestDtos).isEqualTo(List.of(itemRequestDto));
        assertThat(itemRequestDtos.stream().findFirst().get().getItems()).isNull();
    }

    @Test
    void findAllCreatedByOtherWhenUserValidNotHaveRequestThenReturnEmptyList() {
        long userId = 0L;
        int from = 0;
        int size = 1;
        User requestor = new User();
        Page<ItemRequest> itemRequestPage = new PageImpl<>(Collections.emptyList());
        Mockito.when(userService.getUser(userId))
                .thenReturn(requestor);
        Mockito.when(itemRequestRepository.findAll(Mockito.any(BooleanExpression.class), Mockito.any(Pageable.class)))
                .thenReturn(itemRequestPage);

        List<ItemRequestDto> itemRequestDtos = itemRequestService.findAllCreatedByOther(userId, from, size);

        assertThat(itemRequestDtos).asList().isEmpty();
        Mockito.verify(itemRequestMapper, Mockito.never())
                .toItemRequestDto(Mockito.any(), Mockito.any());
    }

    @Test
    void findAllCreatedByOtherWhenUserNotValidThenInterrupt() {
        long userId = 0L;
        int from = 0;
        int size = 1;
        Mockito.when(userService.getUser(userId))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.findAllCreatedByOther(userId, from, size));
        Mockito.verify(itemRequestRepository, Mockito.never())
                .findAll(Mockito.any(BooleanExpression.class), Mockito.any(Pageable.class));
    }

    @Test
    void findAllCreatedByOtherWhenUserValidAndInvalidSampleSizeThenInterrupt() {
        long userId = 0L;
        int from = -1;
        int size = 0;

        assertThrows(IllegalArgumentException.class,
                () -> itemRequestService.findAllCreatedByOther(userId, from, size));
        Mockito.verify(itemRequestRepository, Mockito.never())
                .findAll(Mockito.any(BooleanExpression.class), Mockito.any(Pageable.class));
    }

    @Test
    void getItemRequestWhenUserValidRequestExistsThenReturnRequest() {
        long userId = 0L;
        long itemRequestId = 0L;
        long itemId = 0L;
        User requestor = new User();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestId);
        Item item = new Item();
        item.setId(itemId);
        item.setRequest(itemRequest);
        List<Item> items = List.of(item);
        ItemRequestDto.ItemDto itemDto = new ItemRequestDto.ItemDto();
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setItems(List.of(itemDto));
        Mockito.when(userService.getUser(userId))
                .thenReturn(requestor);
        Mockito.when(itemRequestRepository.findById(itemRequestId))
                .thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRequestMapper.toItemRequestDto(itemRequest, items))
                .thenReturn(itemRequestDto);
        Mockito.when(itemRepository.findAll(Mockito.any(BooleanExpression.class)))
                .thenReturn(List.of(item));

        ItemRequestDto itemRequestDtoReturned = itemRequestService.getItemRequest(userId, itemRequestId);

        assertThat(itemRequestDtoReturned).isEqualTo(itemRequestDto);
        assertThat(itemRequestDtoReturned.getItems().stream().findFirst().get()).isEqualTo(itemDto);
    }

    @Test
    void getItemRequestWhenUserValidRequestExistsWithoutResponseThenReturnRequestWithoutResponse() {
        long userId = 0L;
        long itemRequestId = 0L;
        User requestor = new User();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestId);
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        Mockito.when(userService.getUser(userId))
                .thenReturn(requestor);
        Mockito.when(itemRequestRepository.findById(itemRequestId))
                .thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRequestMapper.toItemRequestDto(itemRequest, Collections.emptyList()))
                .thenReturn(itemRequestDto);
        Mockito.when(itemRepository.findAll(Mockito.any(BooleanExpression.class)))
                .thenReturn(Collections.emptyList());

        ItemRequestDto itemRequestDtoReturned = itemRequestService.getItemRequest(userId, itemRequestId);

        assertThat(itemRequestDtoReturned).isEqualTo(itemRequestDto);
    }

    @Test
    void getItemRequestWhenUserValidRequestNotExistsThenInterrupt() {
        long userId = 0L;
        long itemRequestId = 0L;
        User requestor = new User();
        Mockito.when(userService.getUser(userId))
                .thenReturn(requestor);
        Mockito.when(itemRequestRepository.findById(itemRequestId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.getItemRequest(userId, itemRequestId));
        Mockito.verify(itemRepository, Mockito.never())
                .findAll(Mockito.any(BooleanExpression.class));
    }

    @Test
    void getItemRequestWhenUserNotValidThenInterrupt() {
        long userId = 0L;
        long itemRequestId = 0L;
        Mockito.when(userService.getUser(userId))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.getItemRequest(userId, itemRequestId));
        Mockito.verify(itemRequestRepository, Mockito.never())
                .findById(Mockito.anyLong());
    }
}
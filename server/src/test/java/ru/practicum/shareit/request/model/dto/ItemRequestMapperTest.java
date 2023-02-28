package ru.practicum.shareit.request.model.dto;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestMapperTest {

    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapper();

    @Test
    void toItemRequestDtoWhenValidItemRequestThenReturnItemRequestDto() {
        long itemRequestId = 0L;
        String itemRequestDescription = "description";
        long userId = 0L;
        String userName = "name";
        String userEmail = "email";
        User itemRequestRequestor = new User();
        itemRequestRequestor.setId(userId);
        itemRequestRequestor.setName(userName);
        itemRequestRequestor.setEmail(userEmail);
        LocalDateTime itemRequestUserCreated = LocalDateTime.now();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestId);
        itemRequest.setDescription(itemRequestDescription);
        itemRequest.setRequestor(itemRequestRequestor);
        itemRequest.setCreated(itemRequestUserCreated);

        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);

        assertThat(itemRequestDto.getId()).isEqualTo(itemRequestId);
        assertThat(itemRequestDto.getDescription()).isEqualTo(itemRequestDescription);
        assertThat(itemRequestDto.getCreated()).isEqualTo(itemRequestUserCreated);
        assertThat(itemRequestDto.getItems()).isEmpty();
    }

    @Test
    void testToItemRequestDtoWhenValidItemRequestWithItemThenReturnItemRequestDtoWithItemDto() {
        long itemRequestId = 0L;
        String itemRequestDescription = "description";
        long userId = 1L;
        String userName = "name";
        String userEmail = "email";
        User itemRequestRequestor = new User();
        itemRequestRequestor.setId(userId);
        itemRequestRequestor.setName(userName);
        itemRequestRequestor.setEmail(userEmail);
        LocalDateTime itemRequestUserCreated = LocalDateTime.now();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestId);
        itemRequest.setDescription(itemRequestDescription);
        itemRequest.setRequestor(itemRequestRequestor);
        itemRequest.setCreated(itemRequestUserCreated);
        long itemId = 2L;
        String itemName = "name";
        String itemDescription = "description";
        boolean itemAvailable = true;
        Item item = new Item();
        item.setId(itemId);
        item.setName(itemName);
        item.setDescription(itemDescription);
        item.setAvailable(itemAvailable);
        item.setOwner(itemRequestRequestor);
        item.setRequest(itemRequest);

        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest, List.of(item));

        assertThat(itemRequestDto.getId()).isEqualTo(itemRequestId);
        assertThat(itemRequestDto.getDescription()).isEqualTo(itemRequestDescription);
        assertThat(itemRequestDto.getCreated()).isEqualTo(itemRequestUserCreated);
        assertThat(itemRequestDto.getItems()).hasSize(1);
        ItemRequestDto.ItemDto itemDto = itemRequestDto.getItems().get(0);
        assertThat(itemDto.getId()).isEqualTo(itemId);
        assertThat(itemDto.getName()).isEqualTo(itemName);
        assertThat(itemDto.getOwnerId()).isEqualTo(userId);
        assertThat(itemDto.getDescription()).isEqualTo(itemDescription);
        assertThat(itemDto.getAvailable()).isEqualTo(itemAvailable);
        assertThat(itemDto.getRequestId()).isEqualTo(itemRequestId);
    }

    @Test
    void toItemRequestDtoWhenValidItemRequestWithoutItemThenReturnItemRequestDtoWithoutItem() {
        long itemRequestId = 0L;
        String itemRequestDescription = "description";
        long userId = 0L;
        String userName = "name";
        String userEmail = "email";
        User itemRequestRequestor = new User();
        itemRequestRequestor.setId(userId);
        itemRequestRequestor.setName(userName);
        itemRequestRequestor.setEmail(userEmail);
        LocalDateTime itemRequestUserCreated = LocalDateTime.now();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestId);
        itemRequest.setDescription(itemRequestDescription);
        itemRequest.setRequestor(itemRequestRequestor);
        itemRequest.setCreated(itemRequestUserCreated);

        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest, null);

        assertThat(itemRequestDto.getId()).isEqualTo(itemRequestId);
        assertThat(itemRequestDto.getDescription()).isEqualTo(itemRequestDescription);
        assertThat(itemRequestDto.getCreated()).isEqualTo(itemRequestUserCreated);
        assertThat(itemRequestDto.getItems()).isEmpty();
    }

    @Test
    void toItemRequestWhenValidRequestorAndItemRequestCreateDtoThenReturnItemRequest() {
        String itemRequestCreateDtoDescription = "description";
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto();
        itemRequestCreateDto.setDescription(itemRequestCreateDtoDescription);
        long userId = 0L;
        String userName = "name";
        String userEmail = "email";
        User requestor = new User();
        requestor.setId(userId);
        requestor.setName(userName);
        requestor.setEmail(userEmail);

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(requestor, itemRequestCreateDto);

        assertThat(itemRequest.getDescription()).isEqualTo(itemRequestCreateDtoDescription);
        assertThat(itemRequest.getCreated()).isEqualToIgnoringSeconds(LocalDateTime.now());
        User itemRequestor = itemRequest.getRequestor();
        assertThat(itemRequestor.getId()).isEqualTo(userId);
        assertThat(itemRequestor.getName()).isEqualTo(userName);
        assertThat(itemRequestor.getEmail()).isEqualTo(userEmail);
    }
}
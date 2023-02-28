package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.dto.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.error.exceptions.EntityNotFoundException;
import ru.practicum.shareit.error.exceptions.PermissionException;
import ru.practicum.shareit.error.exceptions.UserRestrictionException;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.model.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.model.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.dto.CommentMapper;
import ru.practicum.shareit.item.comment.storage.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemPatchDto;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private UserService userService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void createItemWhenUserAndItemDtoValidThenReturnItemDto() {
        long userId = 0L;
        long requestId = 1L;
        User owner = new User();
        ItemDto itemDto = new ItemDto();
        itemDto.setRequestId(requestId);
        ItemRequest itemRequest = new ItemRequest();
        String name = "name";
        String description = "description";
        boolean available = true;
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        ItemDto expectedItemDto = new ItemDto();
        expectedItemDto.setName(name);
        expectedItemDto.setDescription(description);
        expectedItemDto.setAvailable(available);
        Mockito.when(userService.getUser(userId))
                .thenReturn(owner);
        Mockito.when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRepository.save(Mockito.any()))
                .thenReturn(item);

        ItemDto itemDtoReturned = itemService.createItem(userId, itemDto);

        assertThat(itemDtoReturned).isEqualTo(expectedItemDto);
    }

    @Test
    void createItemWhenRequestNotExistThenInterrupt() {
        long userId = 0L;
        long requestId = 1L;
        User owner = new User();
        ItemDto itemDto = new ItemDto();
        itemDto.setRequestId(requestId);
        Mockito.when(userService.getUser(userId))
                .thenReturn(owner);
        Mockito.when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> itemService.createItem(userId, itemDto));

        Mockito.verify(itemRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void createItemWhenItemDtoWithoutRequestThenReturnItemDto() {
        long userId = 0L;
        User owner = new User();
        ItemDto itemDto = new ItemDto();
        String name = "name";
        String description = "description";
        boolean available = true;
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        ItemDto expectedItemDto = new ItemDto();
        expectedItemDto.setName(name);
        expectedItemDto.setDescription(description);
        expectedItemDto.setAvailable(available);
        Mockito.when(userService.getUser(userId))
                .thenReturn(owner);
        Mockito.when(itemRepository.save(Mockito.any()))
                .thenReturn(item);

        ItemDto itemDtoReturned = itemService.createItem(userId, itemDto);

        assertThat(itemDtoReturned).isEqualTo(expectedItemDto);
    }

    @Test
    void updateItemWhenUserAndItemValidThenReturnItemDto() {
        long userId = 0L;
        long itemId = 1L;
        User owner = new User();
        owner.setId(userId);
        String patchName = "patch name";
        ItemPatchDto itemPatchDto = new ItemPatchDto();
        itemPatchDto.setName(patchName);
        Item item = new Item();
        item.setOwner(owner);
        String name = "name";
        String description = "description";
        boolean available = true;
        Item patchedItem = new Item();
        patchedItem.setName(name);
        patchedItem.setDescription(description);
        patchedItem.setAvailable(available);
        ItemDto expectedItemDto = new ItemDto();
        expectedItemDto.setName(name);
        expectedItemDto.setDescription(description);
        expectedItemDto.setAvailable(available);
        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(Mockito.any()))
                .thenReturn(patchedItem);

        ItemDto itemDtoReturned = itemService.updateItem(userId, itemId, itemPatchDto);

        assertThat(itemDtoReturned).isEqualTo(expectedItemDto);
    }

    @Test
    void updateItemWhenItemNotExistsThenInterrupt() {
        long userId = 0L;
        long itemId = 1L;
        ItemPatchDto itemPatchDto = new ItemPatchDto();
        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> itemService.updateItem(userId, itemId, itemPatchDto));

        Mockito.verify(itemRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateItemWhenOwnerInvalidThenInterrupt() {
        long userId = 0L;
        long itemId = 1L;
        ItemPatchDto itemPatchDto = new ItemPatchDto();
        User owner = new User();
        owner.setId(2L);
        Item item = new Item();
        item.setOwner(owner);
        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        assertThrows(PermissionException.class,
                () -> itemService.updateItem(userId, itemId, itemPatchDto));

        Mockito.verify(itemRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void getItemWhenUserAndItemValidThenReturnItemDto() {
        long userId = 0L;
        long itemId = 1L;
        User owner = new User();
        owner.setId(userId);
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);
        Booking booking = new Booking();
        Comment comment = new Comment();
        BookingItemDto bookingItemDto = new BookingItemDto();
        CommentDto commentDto = new CommentDto();
        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findFirstByItem_IdAndStatusInAndStartIsBefore(Mockito.anyLong(),
                Mockito.anyList(), Mockito.any(), Mockito.any())).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.findFirstByItem_IdAndStatusInAndStartIsAfter(Mockito.anyLong(),
                Mockito.anyList(), Mockito.any(), Mockito.any())).thenReturn(Optional.of(booking));
        Mockito.when(commentRepository.findAllByItem_Id(Mockito.anyLong()))
                .thenReturn(List.of(comment));
        Mockito.when(bookingMapper.toBookingItemDto(Mockito.any()))
                .thenReturn(bookingItemDto);
        Mockito.when(commentMapper.toCommentDto(Mockito.any()))
                .thenReturn(commentDto);

        ItemDto itemDtoReturned = itemService.getItem(userId, itemId);

        assertThat(itemDtoReturned.getLastBooking()).isEqualTo(bookingItemDto);
        assertThat(itemDtoReturned.getNextBooking()).isEqualTo(bookingItemDto);
        assertThat(itemDtoReturned.getComments()).isEqualTo(List.of(commentDto));
    }

    @Test
    void getItemWhenUserAndOwnerNotEqualThenReturnItemDto() {
        long userId = 0L;
        long itemId = 1L;
        User owner = new User();
        owner.setId(2L);
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);
        Comment comment = new Comment();
        CommentDto commentDto = new CommentDto();
        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findAllByItem_Id(Mockito.anyLong()))
                .thenReturn(List.of(comment));
        Mockito.when(commentMapper.toCommentDto(Mockito.any()))
                .thenReturn(commentDto);

        ItemDto itemDtoReturned = itemService.getItem(userId, itemId);

        assertThat(itemDtoReturned.getComments()).isEqualTo(List.of(commentDto));
    }

    @Test
    void getItemWhenItemNotExistThenInterrupt() {
        long userId = 0L;
        long itemId = 1L;
        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> itemService.getItem(userId, itemId));

        Mockito.verify(commentRepository, Mockito.never()).findAllByItem_Id(Mockito.anyLong());
    }

    @Test
    void getItemsForUserWhenUSerAndItemValidThenReturnItemDto() {
        long userId = 0L;
        long itemId = 1L;
        long commentId = 2L;
        long bookingId = 3L;
        int from = 0;
        int size = 1;
        Item item = new Item();
        item.setId(itemId);
        Page<Item> pageItem = new PageImpl<>(List.of(item));
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now());
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setItem(item);
        CommentDto commentDto = new CommentDto();
        BookingItemDto bookingItemDto = new BookingItemDto();

        Mockito.when(itemRepository.findByOwner_Id(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(pageItem);
        Mockito.when(bookingRepository.findAllByItem_IdInAndStatusIn(Mockito.anyList(), Mockito.anyList()))
                .thenReturn(List.of(booking));
        Mockito.when(commentRepository.findAllByItem_IdIn(Mockito.anyList()))
                .thenReturn(List.of(comment));
        Mockito.when(commentMapper.toCommentDto(Mockito.any()))
                .thenReturn(commentDto);
        Mockito.when(bookingMapper.toBookingItemDto(Mockito.any()))
                .thenReturn(bookingItemDto);

        List<ItemDto> itemDtoReturned = itemService.getItemsForUser(userId, from, size);

        assertThat(itemDtoReturned).hasSize(1);
        assertThat(itemDtoReturned.get(0).getLastBooking()).isEqualTo(bookingItemDto);
        assertThat(itemDtoReturned.get(0).getNextBooking()).isNull();
        assertThat(itemDtoReturned.get(0).getComments()).asList().hasSize(1);
    }

    @Test
    void searchItemsWhenTextValidReturnItemDto() {
        String text = "text";
        int from = 0;
        int size = 1;
        long itemId = 0L;
        Item item = new Item();
        item.setId(itemId);
        Page<Item> pageItem = new PageImpl<>(List.of(item));
        Mockito.when(itemRepository.search(Mockito.anyString(), Mockito.any()))
                .thenReturn(pageItem);

        List<ItemDto> itemDtoReturned = itemService.searchItems(text, from, size);

        assertThat(itemDtoReturned).hasSize(1);
    }

    @Test
    void searchItemsWhenTextBlankReturnEmptyList() {
        String text = "  ";
        int from = 0;
        int size = 1;

        List<ItemDto> itemDtoReturned = itemService.searchItems(text, from, size);

        assertThat(itemDtoReturned).isEmpty();
    }

    @Test
    void addCommentWhenUSerItemAndCommentValidThenReturnCommentDto() {
        long userId = 0L;
        long itemId = 1L;
        User author = new User();
        author.setId(userId);
        Item item = new Item();
        item.setId(itemId);
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        Comment comment = new Comment();
        Booking booking = new Booking();
        CommentDto commentDto = new CommentDto();
        Mockito.when(userService.getUser(userId))
                .thenReturn(author);
        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        Mockito.when(commentMapper.toComment(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(comment);
        Mockito.when(bookingRepository.findAllByBooker_IdAndItem_IdAndStatusInAndEndIsBefore(Mockito.anyLong(),
                        Mockito.anyLong(), Mockito.anyList(), Mockito.any()))
                .thenReturn(List.of(booking));
        Mockito.when(commentRepository.save(Mockito.any()))
                .thenReturn(comment);
        Mockito.when(commentMapper.toCommentDto(Mockito.any()))
                .thenReturn(commentDto);

        CommentDto commentDtoReturned = itemService.addComment(userId, itemId, commentCreateDto);

        assertThat(commentDtoReturned).isEqualTo(commentDto);
    }

    @Test
    void addCommentWhenBookingNotExistThenInterrupt() {
        long userId = 0L;
        long itemId = 1L;
        User author = new User();
        author.setId(userId);
        Item item = new Item();
        item.setId(itemId);
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        Comment comment = new Comment();
        Mockito.when(userService.getUser(userId))
                .thenReturn(author);
        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        Mockito.when(commentMapper.toComment(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(comment);
        Mockito.when(bookingRepository.findAllByBooker_IdAndItem_IdAndStatusInAndEndIsBefore(Mockito.anyLong(),
                        Mockito.anyLong(), Mockito.anyList(), Mockito.any()))
                .thenReturn(Collections.emptyList());

        assertThrows(UserRestrictionException.class, () -> itemService.addComment(userId, itemId, commentCreateDto));

        Mockito.verify(commentRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void addCommentWhenItemNotExistThenInterrupt() {
        long userId = 0L;
        long itemId = 1L;
        User author = new User();
        author.setId(userId);
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        Mockito.when(userService.getUser(userId))
                .thenReturn(author);
        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> itemService.addComment(userId, itemId, commentCreateDto));

        Mockito.verify(commentRepository, Mockito.never()).save(Mockito.any());
    }
}

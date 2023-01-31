package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
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
import ru.practicum.shareit.item.model.dto.ItemMapper;
import ru.practicum.shareit.item.model.dto.ItemPatchDto;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.JsonMergePatchUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final BookingMapper bookingMapper;

    private final CommentMapper commentMapper;

    private final UserService userService;

    @Override
    @Transactional
    public ItemDto createItem(long userId, ItemDto itemDto) {
        User owner = userService.getUser(userId);
        Item item = ItemMapper.toItem(itemDto, owner);
        Item createdItem = itemRepository.save(item);
        return ItemMapper.toItemDto(createdItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(long userId, long itemId, ItemPatchDto itemPatchDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("No item with id : " + itemId));
        if (!item.getOwner().getId().equals(userId)) {
            throw new PermissionException("User doesn't have permission for updating item id :" + item.getId());
        }
        Item itemPatch = ItemMapper.toItem(itemPatchDto, item.getOwner());
        itemPatch.setId(itemId);
        Item patchedItem = JsonMergePatchUtils.mergePatch(item, itemPatch, Item.class);
        patchedItem = itemRepository.save(patchedItem);
        return ItemMapper.toItemDto(patchedItem);
    }

    @Override
    public ItemDto getItem(long userId, long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("No item with id : " + itemId));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (item.getOwner().getId().equals(userId)) {
            itemDto.setLastBooking(getLastBooking(item.getId())
                    .map(bookingMapper::toBookingItemDto).orElse(null));
            itemDto.setNextBooking(getNextBooking(item.getId())
                    .map(bookingMapper::toBookingItemDto).orElse(null));
        }
        List<Comment> comments = getComments(itemId);
        itemDto.setComments(comments.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList()));
        return itemDto;
    }

    private Optional<Booking> getLastBooking(long itemId) {
        return bookingRepository.findFirstByItem_IdAndStatusInAndStartIsBefore(itemId,
                List.of(Status.APPROVED, Status.WAITING), LocalDateTime.now(),
                Sort.by(Sort.Direction.DESC, "start"));
    }

    private Optional<Booking> getNextBooking(long itemId) {
        return bookingRepository.findFirstByItem_IdAndStatusInAndStartIsAfter(itemId,
                List.of(Status.APPROVED, Status.WAITING), LocalDateTime.now(),
                Sort.by(Sort.Direction.ASC, "start"));
    }

    @Override
    public List<ItemDto> getItemsForUser(long userId) {
        List<Item> items = itemRepository.findByOwner_Id(userId);
        List<ItemDto> itemsDto = items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        itemsDto.forEach(item -> item.setLastBooking(getLastBooking(item.getId())
                .map(bookingMapper::toBookingItemDto).orElse(null)));
        itemsDto.forEach(item -> item.setNextBooking(getNextBooking(item.getId())
                .map(bookingMapper::toBookingItemDto).orElse(null)));
        itemsDto.forEach(item -> item.setComments(getComments(item.getId()).stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList())));
        return itemsDto;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(long userId, long itemId, CommentCreateDto commentCreateDto) {
        User author = userService.getUser(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("No item with id : " + itemId));
        Comment comment = commentMapper.toComment(commentCreateDto, author, item);
        List<Booking> bookings = bookingRepository.findAllByBooker_IdAndItem_IdAndStatusInAndEndIsBefore(author.getId(),
                item.getId(), List.of(Status.APPROVED), LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new UserRestrictionException(String.format("User id : %s can't leave comment for item id : %s",
                    author.getId(), item.getId()));
        }
        Comment createdComment = commentRepository.save(comment);
        return commentMapper.toCommentDto(createdComment);
    }

    private List<Comment> getComments(long itemId) {
        return commentRepository.findAllByItem_Id(itemId);
    }

}

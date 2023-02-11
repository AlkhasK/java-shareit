package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
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
import ru.practicum.shareit.item.model.dto.ItemMapper;
import ru.practicum.shareit.item.model.dto.ItemPatchDto;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.JsonMergePatchUtils;
import ru.practicum.shareit.utils.PageUtils;

import java.time.LocalDateTime;
import java.util.*;
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

    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto createItem(long userId, ItemDto itemDto) {
        Item item;
        User owner = userService.getUser(userId);
        if (Objects.nonNull(itemDto.getRequestId())) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NoSuchElementException("No item request id : " + itemDto.getRequestId()));
            item = ItemMapper.toItem(itemDto, itemRequest, owner);
        } else {
            item = ItemMapper.toItem(itemDto, owner);
        }
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
    public List<ItemDto> getItemsForUser(long userId, int from, int size) {
        Map<String, Integer> pageableParam = PageUtils.getPageableParam(from, size);
        Pageable pageable = PageRequest.of(pageableParam.get("page"), pageableParam.get("size"));
        Page<Item> pageItems = itemRepository.findByOwner_Id(userId, pageable);
        List<Item> items = PageUtils.getElements(pageItems.getContent(), from, size);
        List<Long> itemsIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findAllByItem_IdInAndStatusIn(itemsIds,
                List.of(Status.APPROVED, Status.WAITING));
        List<Comment> comments = commentRepository.findAllByItem_IdIn(itemsIds);
        Map<Long, List<CommentDto>> groupedComments = comments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId(),
                        Collectors.mapping(commentMapper::toCommentDto, Collectors.toList())));
        List<ItemDto> itemsDto = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        itemsDto.forEach(item -> {
            item.setLastBooking(getLastBooking(item.getId(), bookings));
            item.setNextBooking(getNextBooking(item.getId(), bookings));
            item.setComments(groupedComments.get(item.getId()));
        });
        return itemsDto;
    }

    private BookingItemDto getLastBooking(long itemId, List<Booking> bookings) {
        Booking lastBooking = bookings.stream()
                .filter(booking -> booking.getItem().getId().equals(itemId))
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getStart))
                .orElse(null);
        return Objects.isNull(lastBooking) ? null : bookingMapper.toBookingItemDto(lastBooking);
    }

    private BookingItemDto getNextBooking(long itemId, List<Booking> bookings) {
        Booking nextBooking = bookings.stream()
                .filter(booking -> booking.getItem().getId().equals(itemId))
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);
        return Objects.isNull(nextBooking) ? null : bookingMapper.toBookingItemDto(nextBooking);
    }

    @Override
    public List<ItemDto> searchItems(String text, int from, int size) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        Map<String, Integer> pageableParam = PageUtils.getPageableParam(from, size);
        Pageable pageable = PageRequest.of(pageableParam.get("page"), pageableParam.get("size"));
        Page<Item> pageItems = itemRepository.search(text, pageable);
        List<Item> items = PageUtils.getElements(pageItems.getContent(), from, size);
        return items.stream()
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

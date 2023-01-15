package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exceptions.EntityNotFoundException;
import ru.practicum.shareit.error.exceptions.PermissionException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    private final UserService userService;

    @Override
    public Item createItem(Item item) {
        userService.getUser(item.getOwnerId());
        return itemStorage.create(item);
    }

    @Override
    public Item updateItem(Item item) {
        userService.getUser(item.getOwnerId());
        long ownerId = getItem(item.getId()).getOwnerId();
        if (item.getOwnerId() != ownerId) {
            throw new PermissionException("String format user hasn't permission to update item id :" + item.getId());
        }
        return itemStorage.update(item);
    }

    @Override
    public Item getItem(long itemId) {
        return itemStorage.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("No item with id : " + itemId));
    }

    @Override
    public List<Item> getItemsForUser(long userId) {
        userService.getUser(userId);
        return itemStorage.findAllForUser(userId);
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemStorage.search(text);
    }

}

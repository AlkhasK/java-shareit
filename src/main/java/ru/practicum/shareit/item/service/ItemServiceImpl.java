package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exceptions.EntityNotFoundException;
import ru.practicum.shareit.error.exceptions.PermissionException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.utils.JsonMergePatchUtils;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    @Override
    public Item createItem(Item item) {
        return itemStorage.create(item);
    }

    @Override
    public Item updateItem(Item patchedItem) {
        Item item = getItem(patchedItem.getId());
        if (!patchedItem.getOwner().equals(item.getOwner())) {
            throw new PermissionException("User doesn't have permission for updating item id :" + item.getId());
        }
        return itemStorage.update(patchedItem);
    }

    @Override
    public Item updateItem(long itemId, Item patch) {
        patch.setId(itemId);
        Item item = getItem(itemId);
        Item patchedItem = JsonMergePatchUtils.mergePatch(item, patch, Item.class);
        return updateItem(patchedItem);
    }

    @Override
    public Item getItem(long itemId) {
        return itemStorage.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("No item with id : " + itemId));
    }

    @Override
    public List<Item> getItemsForUser(long userId) {
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

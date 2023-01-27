package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();

    private final Map<Long, List<Item>> userItems = new HashMap<>();

    private long id = 0;

    @Override
    public List<Item> findAllForUser(long userId) {
        return userItems.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public Optional<Item> findById(long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Item create(Item item) {
        item.setId(getId());
        items.put(item.getId(), item);
        addToUserItems(item);
        return item;
    }

    private void addToUserItems(Item item) {
        long ownerId = item.getOwner().getId();
        List<Item> items = userItems.get(ownerId);
        if (items == null) {
            items = new ArrayList<>();
            items.add(item);
            userItems.put(ownerId, items);
            return;
        }
        items.add(item);
    }

    private long getId() {
        return ++id;
    }

    @Override
    public Item update(Item item) {
        Item oldItem = items.replace(item.getId(), item);
        if (oldItem == null) {
            throw new EntityNotFoundException("No item with id : " + item.getId());
        }
        updateUserItems(item);
        return item;
    }

    private void updateUserItems(Item item) {
        long ownerId = item.getOwner().getId();
        List<Item> items = userItems.get(ownerId);
        items = items.stream()
                .map(i -> i.getId().equals(item.getId()) ? item : i)
                .limit(1)
                .collect(Collectors.toList());
        userItems.put(ownerId, items);
    }

    @Override
    public List<Item> search(String text) {
        Pattern pattern = Pattern.compile(Pattern.quote(text), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> pattern.matcher(item.getName()).find()
                        || pattern.matcher(item.getDescription()).find())
                .collect(Collectors.toList());
    }

}

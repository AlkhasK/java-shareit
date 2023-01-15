package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();

    private long id = 0;

    @Override
    public List<Item> findAllForUser(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> findById(long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Item create(Item item) {
        item.setId(getId());
        items.put(item.getId(), item);
        return item;
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
        return item;
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

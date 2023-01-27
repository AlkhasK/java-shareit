package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    List<Item> findAllForUser(long userId);

    Optional<Item> findById(long itemId);

    Item create(Item item);

    Item update(Item item);

    List<Item> search(String text);
}

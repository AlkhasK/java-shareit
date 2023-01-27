package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item createItem(Item item);

    Item updateItem(Item item);

    Item updateItem(long itemId, Item patch);

    Item getItem(long itemId);

    List<Item> getItemsForUser(long userId);

    List<Item> searchItems(String text);

}

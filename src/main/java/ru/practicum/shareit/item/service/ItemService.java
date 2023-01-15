package ru.practicum.shareit.item.service;

import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

@Validated
public interface ItemService {

    Item createItem(@Valid Item item);

    Item updateItem(@Valid Item item);

    Item getItem(long itemId);

    List<Item> getItemsForUser(long userId);

    List<Item> searchItems(String text);

}

package ru.practicum.shareit.utils;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;

import static org.assertj.core.api.Assertions.assertThat;

class JsonMergePatchUtilsTest {

    @Test
    void mergePatchWhenValidPatchThenReturnPatchedObject() {
        String name = "name";
        String patchName = "patch";
        Item item = new Item();
        item.setName(name);
        Item itemPatch = new Item();
        itemPatch.setName(patchName);

        Item result = JsonMergePatchUtils.mergePatch(item, itemPatch, Item.class);

        assertThat(result.getName()).isEqualTo(patchName);
    }
}
package ru.practicum.shareit.utils;

import java.util.List;
import java.util.Map;

public class PageUtils {

    public static Map<String, Integer> getPageableParam(int from, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must not be less than one");
        }
        if (from < 0) {
            throw new IllegalArgumentException("From must not be less than zero");
        }
        if (from < size) {
            return Map.of("page", 0, "size", size + from);
        } else {
            return Map.of("page", 1, "size", from);
        }
    }

    public static <T> List<T> getElements(List<T> elements, int from, int size) {
        if (from < size) {
            return elements.subList(from, elements.size());
        } else {
            return elements.size() <= size ? elements : elements.subList(0, size);
        }
    }
}

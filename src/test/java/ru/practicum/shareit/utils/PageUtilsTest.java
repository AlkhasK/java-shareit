package ru.practicum.shareit.utils;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PageUtilsTest {

    @Test
    void getPageableParamWhenFromLessSizeThenReturnPageSize() {
        int from = 5;
        int size = 12;
        Map<String, Integer> expectedValues = Map.of("page", 0, "size", 17);

        Map<String, Integer> returnedValue = PageUtils.getPageableParam(from, size);

        assertThat(returnedValue).isEqualTo(expectedValues);
    }

    @Test
    void getPageableParamWhenFromEqualSizeThenReturnPageSize() {
        int from = 5;
        int size = 5;
        Map<String, Integer> expectedValues = Map.of("page", 1, "size", 5);

        Map<String, Integer> returnedValue = PageUtils.getPageableParam(from, size);

        assertThat(returnedValue).isEqualTo(expectedValues);
    }

    @Test
    void getPageableParamWhenSizeLessFromThenReturnPageSize() {
        int from = 12;
        int size = 5;
        Map<String, Integer> expectedValues = Map.of("page", 1, "size", 12);

        Map<String, Integer> returnedValue = PageUtils.getPageableParam(from, size);

        assertThat(returnedValue).isEqualTo(expectedValues);
    }

    @Test
    void getPageableParamWhenInvalidFromThenInterrupt() {
        int from = -1;
        int size = 5;

        assertThrows(IllegalArgumentException.class,
                () -> PageUtils.getPageableParam(from, size));
    }

    @Test
    void getPageableParamWhenInvalidSizeThenInterrupt() {
        int from = 0;
        int size = 0;

        assertThrows(IllegalArgumentException.class,
                () -> PageUtils.getPageableParam(from, size));
    }

    @Test
    void getElementsWhenFromLessSizeThenReturnSublist() {
        int from = 1;
        int size = 3;
        List<String> elements = List.of("a", "a", "a");
        List<String> expected = List.of("a", "a");

        List<String> result = PageUtils.getElements(elements, from, size);

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getElementsWhenFromEqualsSizeThenReturnSublist() {
        int from = 2;
        int size = 2;
        List<String> elements = List.of("a", "a");
        List<String> expected = List.of("a", "a");

        List<String> result = PageUtils.getElements(elements, from, size);

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getElementsSizeLessFromThenReturnSublist() {
        int from = 5;
        int size = 2;
        List<String> elements = List.of("a", "a", "a", "a");
        List<String> expected = List.of("a", "a");

        List<String> result = PageUtils.getElements(elements, from, size);

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expected);
    }
}
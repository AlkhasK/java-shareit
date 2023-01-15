package ru.practicum.shareit.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;

import java.io.IOException;

public class JsonMergePatchUtils {

    public static <T> T mergePatch(T t, JsonNode patch, Class<T> clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode original = mapper.valueToTree(t);
            JsonNode patched = JsonMergePatch.fromJson(patch).apply(original);
            return mapper.treeToValue(patched, clazz);
        } catch (IOException | JsonPatchException e) {
            throw new RuntimeException(e);
        }
    }

}

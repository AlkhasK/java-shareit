package ru.practicum.shareit.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;

import java.io.IOException;

public class JsonMergePatchUtils {

    public static <T> T mergePatch(T t, T p, Class<T> clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            JsonNode original = mapper.valueToTree(t);
            JsonNode patch = mapper.valueToTree(p);
            JsonNode patched = JsonMergePatch.fromJson(patch).apply(original);
            return mapper.treeToValue(patched, clazz);
        } catch (IOException | JsonPatchException e) {
            throw new RuntimeException(e);
        }
    }

}

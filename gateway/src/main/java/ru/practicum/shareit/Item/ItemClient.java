package ru.practicum.shareit.Item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.Item.dto.ItemDto;
import ru.practicum.shareit.Item.dto.ItemPatchDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
@CacheConfig(cacheNames = {"items"})
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    @CacheEvict(cacheNames = "itemRequests", condition = "#result.getStatusCode().is2xxSuccessful()", allEntries = true)
    public ResponseEntity<Object> createItem(long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    @Caching(evict = {@CacheEvict(key = "{#userId, #itemId}", condition = "#result.getStatusCode().is2xxSuccessful()"),
            @CacheEvict(cacheNames = {"bookings", "itemRequests"},
                    condition = "#result.getStatusCode().is2xxSuccessful()", allEntries = true)})
    public ResponseEntity<Object> updateItem(long userId, Long itemId, ItemPatchDto itemPatchDto) {
        return patch("/" + itemId, userId, itemPatchDto);
    }

    @Cacheable(key = "{#userId, #itemId}", unless = "#result.getStatusCode().isError()")
    public ResponseEntity<Object> getItem(long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemsForUser(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> searchItems(String text, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", null, parameters);
    }

}

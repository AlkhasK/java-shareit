package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
@CacheConfig(cacheNames = {"bookings"})
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getBookings(long userId, Status status, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", status.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    @Cacheable(key = "{#userId, #bookingId}", unless = "#result.getStatusCode().isError()")
    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    @Caching(evict = {@CacheEvict(cacheNames = {"items"},
            condition = "#result.getStatusCode().is2xxSuccessful()", allEntries = true)})
    public ResponseEntity<Object> createBooking(long userId, BookingCreateDto bookingCreateDto) {
        return post("", userId, bookingCreateDto);
    }

    @Caching(evict = {@CacheEvict(cacheNames = {"items"},
            condition = "#result.getStatusCode().is2xxSuccessful()", allEntries = true),
            @CacheEvict(key = "{#userId, #bookingId}",
                    condition = "#result.getStatusCode().is2xxSuccessful()")})
    public ResponseEntity<Object> approveBooking(long userId, Long bookingId, Boolean approved) {
        Map<String, Object> parameters = Map.of(
                "approved", approved
        );
        return patch("/" + bookingId + "?approved={approved}", userId, parameters);
    }

    public ResponseEntity<Object> getBookingsItemOwner(long userId, Status status, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", status.name(),
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }

}

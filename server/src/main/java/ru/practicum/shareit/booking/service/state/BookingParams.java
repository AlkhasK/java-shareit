package ru.practicum.shareit.booking.service.state;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
@AllArgsConstructor
public class BookingParams {
    private Long userId;
    private Pageable pageable;
}

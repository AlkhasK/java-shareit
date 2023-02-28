package ru.practicum.shareit.booking.service.state;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.dto.State;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class BookingStrategyFactory {

    private final Map<State, BookingStrategy> strategies;

    @Autowired
    public BookingStrategyFactory(Set<BookingStrategy> strategies) {
        this.strategies = strategies.stream().collect(Collectors.toMap(BookingStrategy::getState, Function.identity(),
                (existing, replacement) -> replacement));
    }

    public BookingStrategy getStrategy(State state) {
        return strategies.get(state);
    }
}

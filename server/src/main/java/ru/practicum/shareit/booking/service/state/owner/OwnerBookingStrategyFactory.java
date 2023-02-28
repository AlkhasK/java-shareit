package ru.practicum.shareit.booking.service.state.owner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.dto.State;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OwnerBookingStrategyFactory {

    private final Map<State, OwnerBookingStrategy> strategies;

    @Autowired
    public OwnerBookingStrategyFactory(Set<OwnerBookingStrategy> strategies) {
        this.strategies = strategies.stream().collect(Collectors.toMap(OwnerBookingStrategy::getState, Function.identity(),
                (existing, replacement) -> replacement));
    }

    public OwnerBookingStrategy getStrategy(State state) {
        return strategies.get(state);
    }
}

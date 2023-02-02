package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker_Id(long bookerId, Sort sort);

    List<Booking> findAllByBooker_IdAndStatus(long bookerId, Status status, Sort sort);

    List<Booking> findAllByBooker_IdAndStatusInAndEndIsBefore(long bookerId, List<Status> status,
                                                              LocalDateTime end, Sort sort);

    List<Booking> findAllByBooker_IdAndStatusInAndStartIsAfter(long bookerId, List<Status> status,
                                                               LocalDateTime start, Sort sort);

    List<Booking> findAllByBooker_IdAndStatusInAndStartIsBeforeAndEndIsAfter(long bookerId, List<Status> status,
                                                                             LocalDateTime start,
                                                                             LocalDateTime end, Sort sort);

    List<Booking> findAllByItem_Owner_Id(long bookerId, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStatus(long bookerId, Status status, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStatusInAndEndIsBefore(long bookerId, List<Status> status,
                                                                  LocalDateTime end, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStatusInAndStartIsAfter(long bookerId, List<Status> status,
                                                                   LocalDateTime start, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStatusInAndStartIsBeforeAndEndIsAfter(long bookerId, List<Status> status,
                                                                                 LocalDateTime start,
                                                                                 LocalDateTime end, Sort sort);

    Optional<Booking> findFirstByItem_IdAndStatusInAndStartIsBefore(long itemId, List<Status> status,
                                                                    LocalDateTime start, Sort sort);

    Optional<Booking> findFirstByItem_IdAndStatusInAndStartIsAfter(long itemId, List<Status> status,
                                                                   LocalDateTime start, Sort sort);

    List<Booking> findAllByBooker_IdAndItem_IdAndStatusInAndEndIsBefore(long bookerId, long itemId, List<Status> status,
                                                                        LocalDateTime end);

    List<Booking> findAllByItem_IdInAndStatusIn(List<Long> itemIds, List<Status> status);
}

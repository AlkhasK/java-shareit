package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByBooker_Id(long bookerId, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndStatus(long bookerId, Status status, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndStatusInAndEndIsBefore(long bookerId, List<Status> status,
                                                              LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndStatusInAndStartIsAfter(long bookerId, List<Status> status,
                                                               LocalDateTime start, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndStatusInAndStartIsBeforeAndEndIsAfter(long bookerId, List<Status> status,
                                                                             LocalDateTime start,
                                                                             LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByItem_Owner_Id(long bookerId, Pageable pageable);

    Page<Booking> findAllByItem_Owner_IdAndStatus(long bookerId, Status status, Pageable pageable);

    Page<Booking> findAllByItem_Owner_IdAndStatusInAndEndIsBefore(long bookerId, List<Status> status,
                                                                  LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByItem_Owner_IdAndStatusInAndStartIsAfter(long bookerId, List<Status> status,
                                                                   LocalDateTime start, Pageable pageable);

    Page<Booking> findAllByItem_Owner_IdAndStatusInAndStartIsBeforeAndEndIsAfter(long bookerId, List<Status> status,
                                                                                 LocalDateTime start,
                                                                                 LocalDateTime end, Pageable pageable);

    Optional<Booking> findFirstByItem_IdAndStatusInAndStartIsBefore(long itemId, List<Status> status,
                                                                    LocalDateTime start, Sort sort);

    Optional<Booking> findFirstByItem_IdAndStatusInAndStartIsAfter(long itemId, List<Status> status,
                                                                   LocalDateTime start, Sort sort);

    List<Booking> findAllByBooker_IdAndItem_IdAndStatusInAndEndIsBefore(long bookerId, long itemId, List<Status> status,
                                                                        LocalDateTime end);

    List<Booking> findAllByItem_IdInAndStatusIn(List<Long> itemIds, List<Status> status);
}

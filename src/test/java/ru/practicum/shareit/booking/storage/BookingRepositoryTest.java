package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User itemOwner;

    private Item item;

    private User booker;

    private Booking booking;

    private final Pageable pageable = PageRequest.of(0, 1);

    @BeforeEach
    void initItemOwner() {
        itemOwner = new User();
        itemOwner.setName("itemOwner");
        itemOwner.setEmail("itemOwner@mail.ru");
    }

    @BeforeEach
    void initBooker() {
        booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@mail.ru");
    }

    @BeforeEach
    void initItem() {
        item = new Item();
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
    }

    @BeforeEach
    void initBooking() {
        booking = new Booking();
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now());
        booking.setStatus(Status.APPROVED);
    }

    @Test
    void findAllByBooker_Id() {
        User createdItemOwner = entityManager.persistAndFlush(itemOwner);
        item.setOwner(createdItemOwner);
        Item createdItem = entityManager.persistAndFlush(item);
        User createdBooker = entityManager.persistAndFlush(booker);
        booking.setBooker(createdBooker);
        booking.setItem(createdItem);
        Booking createdBooking = entityManager.persistAndFlush(booking);

        Page<Booking> bookingExtract = bookingRepository.findAllByBooker_Id(createdBooking.getBooker().getId(), pageable);

        assertThat(bookingExtract.getContent().get(0).getId()).isEqualTo(createdBooking.getId());
    }

    @Test
    void findAllByBooker_IdAndStatus() {
        User createdItemOwner = entityManager.persistAndFlush(itemOwner);
        item.setOwner(createdItemOwner);
        Item createdItem = entityManager.persistAndFlush(item);
        User createdBooker = entityManager.persistAndFlush(booker);
        booking.setBooker(createdBooker);
        booking.setItem(createdItem);
        Booking createdBooking = entityManager.persistAndFlush(booking);

        Page<Booking> bookingExtract = bookingRepository.findAllByBooker_IdAndStatus(createdBooking.getBooker().getId(),
                Status.APPROVED, pageable);

        assertThat(bookingExtract.getContent().get(0).getId()).isEqualTo(createdBooking.getId());
    }

    @Test
    void findAllByBooker_IdAndStatusInAndEndIsBefore() {
        User createdItemOwner = entityManager.persistAndFlush(itemOwner);
        item.setOwner(createdItemOwner);
        Item createdItem = entityManager.persistAndFlush(item);
        User createdBooker = entityManager.persistAndFlush(booker);
        booking.setBooker(createdBooker);
        booking.setItem(createdItem);
        Booking createdBooking = entityManager.persistAndFlush(booking);

        Page<Booking> bookingExtract = bookingRepository
                .findAllByBooker_IdAndStatusInAndEndIsBefore(createdBooking.getBooker().getId(),
                        List.of(Status.APPROVED), LocalDateTime.now().plusHours(1), pageable);

        assertThat(bookingExtract.getContent().get(0).getId()).isEqualTo(createdBooking.getId());
    }

    @Test
    void findAllByBooker_IdAndStatusInAndStartIsAfter() {
        User createdItemOwner = entityManager.persistAndFlush(itemOwner);
        item.setOwner(createdItemOwner);
        Item createdItem = entityManager.persistAndFlush(item);
        User createdBooker = entityManager.persistAndFlush(booker);
        booking.setBooker(createdBooker);
        booking.setItem(createdItem);
        Booking createdBooking = entityManager.persistAndFlush(booking);

        Page<Booking> bookingExtract = bookingRepository
                .findAllByBooker_IdAndStatusInAndStartIsAfter(createdBooking.getBooker().getId(),
                        List.of(Status.APPROVED), LocalDateTime.now().minusHours(1), pageable);

        assertThat(bookingExtract.getContent().get(0).getId()).isEqualTo(createdBooking.getId());
    }

    @Test
    void findAllByBooker_IdAndStatusInAndStartIsBeforeAndEndIsAfter() {
        User createdItemOwner = entityManager.persistAndFlush(itemOwner);
        item.setOwner(createdItemOwner);
        Item createdItem = entityManager.persistAndFlush(item);
        User createdBooker = entityManager.persistAndFlush(booker);
        booking.setBooker(createdBooker);
        booking.setItem(createdItem);
        Booking createdBooking = entityManager.persistAndFlush(booking);

        Page<Booking> bookingExtract = bookingRepository
                .findAllByBooker_IdAndStatusInAndStartIsBeforeAndEndIsAfter(createdBooking.getBooker().getId(),
                        List.of(Status.APPROVED), LocalDateTime.now().plusHours(1), LocalDateTime.now().minusHours(1),
                        pageable);

        assertThat(bookingExtract.getContent().get(0).getId()).isEqualTo(createdBooking.getId());
    }

    @Test
    void findAllByItem_Owner_Id() {
        User createdItemOwner = entityManager.persistAndFlush(itemOwner);
        item.setOwner(createdItemOwner);
        Item createdItem = entityManager.persistAndFlush(item);
        User createdBooker = entityManager.persistAndFlush(booker);
        booking.setBooker(createdBooker);
        booking.setItem(createdItem);
        Booking createdBooking = entityManager.persistAndFlush(booking);

        Page<Booking> bookingExtract = bookingRepository
                .findAllByItem_Owner_Id(createdItemOwner.getId(), pageable);

        assertThat(bookingExtract.getContent().get(0).getId()).isEqualTo(createdBooking.getId());
    }

    @Test
    void findAllByItem_Owner_IdAndStatus() {
        User createdItemOwner = entityManager.persistAndFlush(itemOwner);
        item.setOwner(createdItemOwner);
        Item createdItem = entityManager.persistAndFlush(item);
        User createdBooker = entityManager.persistAndFlush(booker);
        booking.setBooker(createdBooker);
        booking.setItem(createdItem);
        Booking createdBooking = entityManager.persistAndFlush(booking);

        Page<Booking> bookingExtract = bookingRepository
                .findAllByItem_Owner_IdAndStatus(createdItemOwner.getId(), Status.APPROVED, pageable);

        assertThat(bookingExtract.getContent().get(0).getId()).isEqualTo(createdBooking.getId());
    }

    @Test
    void findAllByItem_Owner_IdAndStatusInAndEndIsBefore() {
        User createdItemOwner = entityManager.persistAndFlush(itemOwner);
        item.setOwner(createdItemOwner);
        Item createdItem = entityManager.persistAndFlush(item);
        User createdBooker = entityManager.persistAndFlush(booker);
        booking.setBooker(createdBooker);
        booking.setItem(createdItem);
        Booking createdBooking = entityManager.persistAndFlush(booking);

        Page<Booking> bookingExtract = bookingRepository
                .findAllByItem_Owner_IdAndStatusInAndEndIsBefore(createdItemOwner.getId(), List.of(Status.APPROVED),
                        LocalDateTime.now().plusHours(1), pageable);

        assertThat(bookingExtract.getContent().get(0).getId()).isEqualTo(createdBooking.getId());
    }

    @Test
    void findAllByItem_Owner_IdAndStatusInAndStartIsAfter() {
        User createdItemOwner = entityManager.persistAndFlush(itemOwner);
        item.setOwner(createdItemOwner);
        Item createdItem = entityManager.persistAndFlush(item);
        User createdBooker = entityManager.persistAndFlush(booker);
        booking.setBooker(createdBooker);
        booking.setItem(createdItem);
        Booking createdBooking = entityManager.persistAndFlush(booking);

        Page<Booking> bookingExtract = bookingRepository
                .findAllByItem_Owner_IdAndStatusInAndStartIsAfter(createdItemOwner.getId(), List.of(Status.APPROVED),
                        LocalDateTime.now().minusHours(1), pageable);

        assertThat(bookingExtract.getContent().get(0).getId()).isEqualTo(createdBooking.getId());
    }

    @Test
    void findAllByItem_Owner_IdAndStatusInAndStartIsBeforeAndEndIsAfter() {
        User createdItemOwner = entityManager.persistAndFlush(itemOwner);
        item.setOwner(createdItemOwner);
        Item createdItem = entityManager.persistAndFlush(item);
        User createdBooker = entityManager.persistAndFlush(booker);
        booking.setBooker(createdBooker);
        booking.setItem(createdItem);
        Booking createdBooking = entityManager.persistAndFlush(booking);

        Page<Booking> bookingExtract = bookingRepository
                .findAllByItem_Owner_IdAndStatusInAndStartIsBeforeAndEndIsAfter(createdItemOwner.getId(), List.of(Status.APPROVED),
                        LocalDateTime.now().plusHours(1), LocalDateTime.now().minusHours(1), pageable);

        assertThat(bookingExtract.getContent().get(0).getId()).isEqualTo(createdBooking.getId());
    }

    @Test
    void findFirstByItem_IdAndStatusInAndStartIsBefore() {
        User createdItemOwner = entityManager.persistAndFlush(itemOwner);
        item.setOwner(createdItemOwner);
        Item createdItem = entityManager.persistAndFlush(item);
        User createdBooker = entityManager.persistAndFlush(booker);
        booking.setBooker(createdBooker);
        booking.setItem(createdItem);
        entityManager.persistAndFlush(booking);

        Optional<Booking> bookingExtract = bookingRepository
                .findFirstByItem_IdAndStatusInAndStartIsBefore(createdItem.getId(), List.of(Status.APPROVED),
                        LocalDateTime.now().plusHours(1), Sort.unsorted());

        assertThat(bookingExtract).isPresent();
    }

    @Test
    void findFirstByItem_IdAndStatusInAndStartIsAfter() {
        User createdItemOwner = entityManager.persistAndFlush(itemOwner);
        item.setOwner(createdItemOwner);
        Item createdItem = entityManager.persistAndFlush(item);
        User createdBooker = entityManager.persistAndFlush(booker);
        booking.setBooker(createdBooker);
        booking.setItem(createdItem);
        entityManager.persistAndFlush(booking);

        Optional<Booking> bookingExtract = bookingRepository
                .findFirstByItem_IdAndStatusInAndStartIsAfter(createdItem.getId(), List.of(Status.APPROVED),
                        LocalDateTime.now().minusHours(1), Sort.unsorted());

        assertThat(bookingExtract).isPresent();
    }

    @Test
    void findAllByBooker_IdAndItem_IdAndStatusInAndEndIsBefore() {
        User createdItemOwner = entityManager.persistAndFlush(itemOwner);
        item.setOwner(createdItemOwner);
        Item createdItem = entityManager.persistAndFlush(item);
        User createdBooker = entityManager.persistAndFlush(booker);
        booking.setBooker(createdBooker);
        booking.setItem(createdItem);
        entityManager.persistAndFlush(booking);

        List<Booking> bookingExtract = bookingRepository
                .findAllByBooker_IdAndItem_IdAndStatusInAndEndIsBefore(createdBooker.getId(), createdItem.getId(),
                        List.of(Status.APPROVED), LocalDateTime.now().plusHours(1));

        assertThat(bookingExtract).asList().hasSize(1);
    }

    @Test
    void findAllByItem_IdInAndStatusIn() {
        User createdItemOwner = entityManager.persistAndFlush(itemOwner);
        item.setOwner(createdItemOwner);
        Item createdItem = entityManager.persistAndFlush(item);
        User createdBooker = entityManager.persistAndFlush(booker);
        booking.setBooker(createdBooker);
        booking.setItem(createdItem);
        entityManager.persistAndFlush(booking);

        List<Booking> bookingExtract = bookingRepository
                .findAllByItem_IdInAndStatusIn(List.of(createdItem.getId()), List.of(Status.APPROVED));

        assertThat(bookingExtract).asList().hasSize(1);
    }
}
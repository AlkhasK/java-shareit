package ru.practicum.shareit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.ControllerConstants;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@ActiveProfiles("test")
@Transactional
public class BookingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private BookingRepository bookingRepository;

    private User userOwner;

    private User userBooker;

    private Item item;

    private BookingCreateDto bookingCreateDto;

    @BeforeEach
    void initUser() {
        userOwner = new User();
        userOwner.setName("name");
        userOwner.setEmail("email owner");

        userBooker = new User();
        userBooker.setName("name");
        userBooker.setEmail("email booker");
    }

    @BeforeEach
    void initItem() {
        item = new Item();
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
    }

    @BeforeEach
    void initBookingCreateDto() {
        bookingCreateDto = new BookingCreateDto();
    }

    @SneakyThrows
    @Test
    void createBookingTest() {
        LocalDateTime expectedStart = LocalDateTime.now().plusMinutes(1);
        LocalDateTime expectedEnd = expectedStart.plusMinutes(2);
        Status expectedStatus = Status.WAITING;
        User dbUserOwner = testEntityManager.persistAndFlush(userOwner);
        item.setOwner(dbUserOwner);
        Item dbItem = testEntityManager.persistAndFlush(item);
        User dbUserBooker = testEntityManager.persistAndFlush(userBooker);
        bookingCreateDto.setItemId(dbItem.getId());
        bookingCreateDto.setStart(expectedStart);
        bookingCreateDto.setEnd(expectedEnd);

        String result = mockMvc.perform(post("/bookings")
                        .header(ControllerConstants.USER_ID_HEADER, dbUserBooker.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookingDto returnedBookingDto = objectMapper.readValue(result, BookingDto.class);
        Optional<Booking> dbBooking = bookingRepository.findById(returnedBookingDto.getId());
        assertThat(dbBooking).isPresent();
        assertThat(dbBooking.get().getStart()).isEqualToIgnoringSeconds(expectedStart);
        assertThat(dbBooking.get().getEnd()).isEqualToIgnoringSeconds(expectedEnd);
        assertThat(dbBooking.get().getStatus()).isEqualTo(expectedStatus);
    }
}

package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.utils.ControllerConstants;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    private final long userId = 0L;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;
    private BookingDto bookingDto;

    @BeforeEach
    void initBookingDto() {
        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now());
        bookingDto.setStatus(Status.APPROVED);
        ItemDto itemDto = new ItemDto();
        itemDto.setId(2L);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        UserDto userDto = new UserDto();
        userDto.setId(3L);
        userDto.setName("name");
        userDto.setEmail("user@tmail.ru");
        bookingDto.setItem(itemDto);
        bookingDto.setBooker(userDto);
    }

    @SneakyThrows
    @Test
    void createWhenDataValidThenReturnBookingDto() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(2L);
        bookingCreateDto.setStart(LocalDateTime.now().plusHours(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusHours(2));
        Mockito.when(bookingService.createBooking(Mockito.anyLong(), Mockito.any()))
                .thenReturn(bookingDto);

        String result = mockMvc.perform(post("/bookings")
                        .header(ControllerConstants.USER_ID_HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(objectMapper.writeValueAsString(bookingDto));
    }

    @SneakyThrows
    @Test
    void approveWhenDataValidThenReturnBookingDto() {
        long bookingId = 1L;
        Mockito.when(bookingService.approveBooking(userId, bookingId, true))
                .thenReturn(bookingDto);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(ControllerConstants.USER_ID_HEADER, userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(objectMapper.writeValueAsString(bookingDto));
    }

    @SneakyThrows
    @Test
    void findWhenDataValidThenReturnBookingDto() {
        long bookingId = 1L;
        Mockito.when(bookingService.getBooking(userId, bookingId))
                .thenReturn(bookingDto);

        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(ControllerConstants.USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(objectMapper.writeValueAsString(bookingDto));
    }

    @SneakyThrows
    @Test
    void findAllForUserWhenDataValidThenReturnBookingDto() {
        String state = "ALL";
        int from = 0;
        int size = 1;
        Mockito.when(bookingService.getBookings(userId, state, from, size))
                .thenReturn(List.of(bookingDto));

        String result = mockMvc.perform(get("/bookings")
                        .header(ControllerConstants.USER_ID_HEADER, userId)
                        .param("state", state)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(objectMapper.writeValueAsString(List.of(bookingDto)));
    }

    @SneakyThrows
    @Test
    void findAllForUserItemOwnerWhenDataValidThenReturnBookingDto() {
        String state = "ALL";
        int from = 0;
        int size = 1;
        Mockito.when(bookingService.getBookingsItemOwner(userId, state, from, size))
                .thenReturn(List.of(bookingDto));

        String result = mockMvc.perform(get("/bookings/owner")
                        .header(ControllerConstants.USER_ID_HEADER, userId)
                        .param("state", state)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(objectMapper.writeValueAsString(List.of(bookingDto)));
    }
}
package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.error.exceptions.EntityNotFoundException;
import ru.practicum.shareit.request.model.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.utils.ControllerConstants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private final long userId = 0L;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void initItemRequestDto() {
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(0L);
        itemRequestDto.setDescription("description");
        itemRequestDto.setCreated(LocalDateTime.now());

        ItemRequestDto.ItemDto itemDto = new ItemRequestDto.ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setOwnerId(2L);
        itemDto.setDescription("item description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(0L);

        itemRequestDto.setItems(List.of(itemDto));
    }


    @SneakyThrows
    @Test
    void createWhenItemRequestCreateDtoAndUserValidThenReturnItemRequestDto() {
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto();
        itemRequestCreateDto.setDescription("description");
        Mockito.when(itemRequestService.createItemRequest(userId, itemRequestCreateDto))
                .thenReturn(itemRequestDto);

        String result = mockMvc.perform(post("/requests")
                        .header(ControllerConstants.USER_ID_HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestCreateDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(objectMapper.writeValueAsString(itemRequestDto));
    }

    @SneakyThrows
    @Test
    void createWhenItemRequestCreateDtoNotValidThenReturnBadRequest() {
        StringBuilder description = new StringBuilder();
        Stream.generate(() -> "a").limit(513).forEach(description::append);
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto();
        itemRequestCreateDto.setDescription(description.toString());
        Mockito.when(itemRequestService.createItemRequest(userId, itemRequestCreateDto))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header(ControllerConstants.USER_ID_HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestCreateDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void createWhenInvalidUserThenReturnNotFound() {
        String errorMessage = "error";
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto();
        itemRequestCreateDto.setDescription("description");
        Mockito.when(itemRequestService.createItemRequest(userId, itemRequestCreateDto))
                .thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(post("/requests")
                        .header(ControllerConstants.USER_ID_HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestCreateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(errorMessage)));
    }

    @SneakyThrows
    @Test
    void findAllCreatedByUserWhenUserValidAndHaveRequestThenReturnRequest() {
        Mockito.when(itemRequestService.getAllItemRequestCreatedByUser(userId))
                .thenReturn(List.of(itemRequestDto));

        String result = mockMvc.perform(get("/requests")
                        .header(ControllerConstants.USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(objectMapper.writeValueAsString(List.of(itemRequestDto)));
    }

    @SneakyThrows
    @Test
    void findAllCreatedByUserWhenInvalidUserThenReturnInternalServerError() {
        mockMvc.perform(get("/requests")
                        .header(ControllerConstants.USER_ID_HEADER, "userId"))
                .andExpect(status().isInternalServerError());

        Mockito.verify(itemRequestService, Mockito.never()).getAllItemRequestCreatedByUser(userId);
    }

    @SneakyThrows
    @Test
    void findAllCreatedByOtherWhenUserValidReturnOk() {
        Mockito.when(itemRequestService.findAllCreatedByOther(userId, 0, 10))
                .thenReturn(List.of(itemRequestDto));

        String result = mockMvc.perform(get("/requests/all")
                        .header(ControllerConstants.USER_ID_HEADER, userId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(objectMapper.writeValueAsString(List.of(itemRequestDto)));
    }

    @SneakyThrows
    @Test
    void findWhenUserAndRequestValidThenReturnItemRequestDto() {
        long requestId = 0;
        Mockito.when(itemRequestService.getItemRequest(userId, requestId))
                .thenReturn(itemRequestDto);

        String result = mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(ControllerConstants.USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(objectMapper.writeValueAsString(itemRequestDto));
    }
}
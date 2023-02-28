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
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
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
public class ItemRequestTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user;

    private ItemRequestCreateDto itemRequestCreateDto;

    @BeforeEach
    void initUser() {
        user = new User();
        user.setName("name");
        user.setEmail("email");
    }

    @BeforeEach
    void initItemRequestCreateDto() {
        itemRequestCreateDto = new ItemRequestCreateDto();
        itemRequestCreateDto.setDescription("description");
    }

    @SneakyThrows
    @Test
    void createItemRequestTest() {
        User dbUser = testEntityManager.persistAndFlush(user);

        String result = mockMvc.perform(post("/requests")
                        .header(ControllerConstants.USER_ID_HEADER, dbUser.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestCreateDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ItemRequestDto returnedItemRequestDto = objectMapper.readValue(result, ItemRequestDto.class);
        Optional<ItemRequest> dbItemRequest = itemRequestRepository.findById(returnedItemRequestDto.getId());
        assertThat(dbItemRequest).isPresent();
        assertThat(dbItemRequest.get().getDescription()).isEqualTo(itemRequestCreateDto.getDescription());
        assertThat(dbItemRequest.get().getRequestor().getId()).isEqualTo(dbUser.getId());
        assertThat(dbItemRequest.get().getCreated()).isBefore(LocalDateTime.now());
    }
}

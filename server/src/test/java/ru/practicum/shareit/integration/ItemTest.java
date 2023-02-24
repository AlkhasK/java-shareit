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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.ControllerConstants;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@ActiveProfiles("test")
@Transactional
public class ItemTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private ItemDto itemDto;

    private User user;

    @BeforeEach
    void initItemDto() {
        itemDto = new ItemDto();
        itemDto.setDescription("description");
        itemDto.setName("name");
        itemDto.setAvailable(true);
    }

    @BeforeEach
    void initUser() {
        user = new User();
        user.setName("name");
        user.setEmail("email");
    }

    @SneakyThrows
    @Test
    void createItemTest() {
        User dbUser = testEntityManager.persistAndFlush(user);

        String result = mockMvc.perform(post("/items")
                        .header(ControllerConstants.USER_ID_HEADER, dbUser.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ItemDto returnedItemDto = objectMapper.readValue(result, ItemDto.class);
        Optional<Item> dbItem = itemRepository.findById(returnedItemDto.getId());
        assertThat(dbItem).isPresent();
        assertThat(dbItem.get().getName()).isEqualTo(itemDto.getName());
        assertThat(dbItem.get().getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(dbItem.get().getAvailable()).isEqualTo(itemDto.getAvailable());
    }
}

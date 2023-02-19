package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.model.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemPatchDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utils.ControllerConstants;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    private final long userId = 0L;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mockMvc;
    private ItemDto itemDto;

    @BeforeEach
    void initItemDto() {
        itemDto = new ItemDto();
        itemDto.setId(0L);
        itemDto.setDescription("description");
        itemDto.setName("name");
        itemDto.setAvailable(true);
    }

    @SneakyThrows
    @Test
    void createWhenUserAndItemCreateDtoValidThenReturnItemDto() {
        Mockito.when(itemService.createItem(userId, itemDto))
                .thenReturn(itemDto);

        String result = mockMvc.perform(post("/items")
                        .header(ControllerConstants.USER_ID_HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(objectMapper.writeValueAsString(itemDto));
    }

    @SneakyThrows
    @Test
    void updateWhenUserAndItemPatchDtoValidThenReturnItemDto() {
        long itemId = 1L;
        ItemPatchDto itemPatchDto = new ItemPatchDto();
        Mockito.when(itemService.updateItem(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(itemDto);

        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header(ControllerConstants.USER_ID_HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemPatchDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(objectMapper.writeValueAsString(itemDto));
    }

    @SneakyThrows
    @Test
    void findWhenItemExistThenReturnItemDto() {
        long itemId = 1L;
        Mockito.when(itemService.getItem(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemDto);

        String result = mockMvc.perform(get("/items/{itemId}", itemId)
                        .header(ControllerConstants.USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(objectMapper.writeValueAsString(itemDto));
    }

    @SneakyThrows
    @Test
    void findAllForUserWhenUserHaveItemThenReturnItemDto() {
        Mockito.when(itemService.getItemsForUser(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(itemDto));

        String result = mockMvc.perform(get("/items")
                        .header(ControllerConstants.USER_ID_HEADER, userId)
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(objectMapper.writeValueAsString(List.of(itemDto)));
    }

    @SneakyThrows
    @Test
    void searchWhenItemWithTextExistThenReturnItemDto() {
        String text = "text";
        Mockito.when(itemService.searchItems(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(itemDto));

        String result = mockMvc.perform(get("/items/search")
                        .header(ControllerConstants.USER_ID_HEADER, userId)
                        .param("text", text)
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(objectMapper.writeValueAsString(List.of(itemDto)));
    }

    @SneakyThrows
    @Test
    void addCommentWhenItemAndCommentValidTheReturnCommentDto() {
        long itemId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setId(2L);
        commentDto.setText("text");
        commentDto.setAuthorName("name");
        commentDto.setCreated(LocalDateTime.now());
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("text");
        Mockito.when(itemService.addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(commentDto);

        String result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(ControllerConstants.USER_ID_HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(objectMapper.writeValueAsString(commentDto));
    }
}
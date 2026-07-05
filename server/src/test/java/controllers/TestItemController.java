package controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.ShareItServerApplication;
import ru.practicum.controller.ItemController;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.ItemDto;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@ContextConfiguration(classes = ShareItServerApplication.class)
class TestItemController extends TestControllerBase {

    @Test
    void createItem_shouldReturnCreatedItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);

        ItemDto savedItem = new ItemDto();
        savedItem.setId(1L);
        savedItem.setName("Test Item");

        when(itemService.createItem(itemDto, 1L)).thenReturn(savedItem);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Item"));
    }

    @Test
    void updateItem_shouldReturnUpdatedItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item");
        itemDto.setDescription("Updated Description");

        ItemDto updatedItem = new ItemDto();
        updatedItem.setId(1L);
        updatedItem.setName("Updated Item");

        when(itemService.updateItem(itemDto, 1L, 1L)).thenReturn(updatedItem);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getItemById_shouldReturnItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        when(itemService.getItemById(1L, 1L)).thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getItemsByOwner_shouldReturnAllItems() throws Exception {
        ItemDto item1 = new ItemDto();
        item1.setId(1L);
        item1.setName("Item 1");
        item1.setDescription("Desc 1");
        item1.setAvailable(true);

        ItemDto item2 = new ItemDto();
        item2.setId(2L);
        item2.setName("Item 2");
        item2.setDescription("Desc 2");
        item2.setAvailable(false);

        List<ItemDto> items = List.of(item1, item2);
        when(itemService.getItemsByOwner(1L)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Item 1"));
    }

    @Test
    void searchItems_shouldReturnMatchingItems() throws Exception {
        List<ItemDto> items = List.of(new ItemDto());
        items.get(0).setName("Searchable Item");
        when(itemService.searchItems("search")).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Searchable Item"));
    }

    @Test
    void addComment_shouldCreateComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        CommentDto savedComment = new CommentDto();
        savedComment.setId(1L);
        savedComment.setText("Great item!");
        savedComment.setAuthorName("Test User");

        when(itemService.addComment(1L, 1L, commentDto)).thenReturn(savedComment);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Great item!"));
    }
}
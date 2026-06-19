package ru.practicum.item;

import ru.practicum.dto.CommentDto;
import ru.practicum.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId);

    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> getItemsByOwner(Long userId);

    List<ItemDto> searchItems(String text);

    CommentDto addComment(Long itemId, Long authorId, CommentDto commentDto);
}

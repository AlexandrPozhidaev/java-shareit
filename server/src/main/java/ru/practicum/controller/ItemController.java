package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.ItemDto;
import ru.practicum.item.ItemService;

import java.util.List;

import static ru.practicum.Header.HEADER;

@Controller
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(
            @RequestHeader(HEADER) @Positive Long userId,
            @Valid @RequestBody ItemDto itemDto) {

        log.debug("ItemDto: name='{}', description='{}', available={}",
                itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());

        ItemDto savedItem = itemService.createItem(itemDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(
            @PathVariable @Positive Long itemId,
            @RequestHeader(HEADER) @Positive Long userId,
            @RequestBody ItemDto itemDto) {

        itemDto.setId(itemId);
        ItemDto updatedItem = itemService.updateItem(itemDto, itemId, userId);
        return ResponseEntity.ok(updatedItem);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(
            @PathVariable @Positive Long itemId,
            @RequestHeader(HEADER) @Positive Long userId) {

        ItemDto itemDto = itemService.getItemById(itemId, userId);
        return ResponseEntity.ok(itemDto);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItemsByOwner(
            @RequestHeader(HEADER) @Positive Long userId) {

        List<ItemDto> items = itemService.getItemsByOwner(userId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(
            @RequestParam String text) {

        List<ItemDto> items = itemService.searchItems(text);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable @Positive Long itemId,
            @RequestHeader(HEADER) @Positive Long authorId,
            @RequestBody CommentDto commentDto) {

        CommentDto savedComment = itemService.addComment(itemId, authorId, commentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
    }
}

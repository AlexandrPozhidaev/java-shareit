package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwner(item.getOwner() != null ? item.getOwner().getId() : null);
        dto.setRequest(item.getRequest() != null ? item.getRequest().getId() : null);
        dto.setLastBooking(null);
        dto.setNextBooking(null);
        dto.setComments(new ArrayList<>());

        return dto;
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() == null) {
            item.setAvailable(true);
        } else {
            item.setAvailable(itemDto.getAvailable());
        }
        return item;

    }
}
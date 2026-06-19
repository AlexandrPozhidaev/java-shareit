package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.BookingDto;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.ItemDto;
import ru.practicum.item.Item;
import ru.practicum.itemRequest.ItemRequest;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwner(item.getOwner() != null ? item.getOwner().getId() : null);
        if (item.getRequest() != null) {
            dto.setRequestId(item.getRequest().getId());
        } else {
            dto.setRequestId(null);
        }
        dto.setLastBooking(null);
        dto.setNextBooking(null);
        dto.setComments(new ArrayList<>());

        return dto;
    }

    public static Item toItem(ItemDto itemDto, ItemRequest request) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() == null) {
            item.setAvailable(true);
        } else {
            item.setAvailable(itemDto.getAvailable());
        }
        item.setRequest(request);
        return item;

    }

    public static ItemDto toItemDtoForBooking(Item item) {
        if (item == null) {
            return null;
        }
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwner(item.getOwner() != null ? item.getOwner().getId() : null);
        return dto;
    }

    public static ItemDto toItemDtoWithDetails(Item item,
                                               BookingDto lastBooking,
                                               BookingDto nextBooking,
                                               List<CommentDto> comments) {
        ItemDto dto = toItemDto(item);
        dto.setLastBooking(lastBooking);
        dto.setNextBooking(nextBooking);
        dto.setComments(comments);
        return dto;
    }
}
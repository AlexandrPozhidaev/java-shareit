package ru.practicum.mapper;

import ru.practicum.dto.ItemDto;
import ru.practicum.dto.ItemRequestDto;
import ru.practicum.itemRequest.ItemRequest;

import java.util.List;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDto dto) {
        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        return request;
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        dto.setItems(null);
        return dto;
    }

    public static ItemRequestDto toItemRequestDtoWithItems(ItemRequest request, List<ItemDto> items) {
        ItemRequestDto dto = toItemRequestDto(request);
        dto.setItems(items);
        return dto;
    }

}

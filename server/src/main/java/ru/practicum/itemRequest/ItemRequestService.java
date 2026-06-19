package ru.practicum.itemRequest;

import ru.practicum.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(ItemRequestDto requestDto, Long requestorId);

    List<ItemRequestDto> getUserRequests(Long userId);

    List<ItemRequestDto> getAllRequests();

    ItemRequestDto getRequestById(Long requestId);
}

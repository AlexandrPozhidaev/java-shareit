package ru.practicum.itemRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.ItemDto;
import ru.practicum.dto.ItemRequestDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.ItemRepository;
import ru.practicum.mapper.ItemMapper;
import ru.practicum.mapper.ItemRequestMapper;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ItemRequestServiceImpl(ItemRequestRepository requestRepository,
                                  UserRepository userRepository,
                                  ItemRepository itemRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    public ItemRequestDto createRequest(ItemRequestDto requestDto, Long requestorId) {
        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemRequest request = ItemRequestMapper.toItemRequest(requestDto);
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());

        ItemRequest savedRequest = requestRepository.save(request);
        return ItemRequestMapper.toItemRequestDto(savedRequest);
    }

    public List<ItemRequestDto> getUserRequests(Long userId) {
        List<ItemRequest> requests = requestRepository.findByRequestorIdOrderByCreatedDesc(userId);
        return requests.stream()
                .map(request -> {
                    List<ItemDto> availableItems = itemRepository.findByRequestId(request.getId()).stream()
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList());
                    return ItemRequestMapper.toItemRequestDtoWithItems(request, availableItems);
                })
                .collect(Collectors.toList());
    }

    public List<ItemRequestDto> getAllRequests() {
        List<ItemRequest> requests = requestRepository.findAllByOrderByCreatedDesc();
        return requests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    public ItemRequestDto getRequestById(Long requestId) {
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        List<ItemDto> availableItems = itemRepository.findByRequestId(requestId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        return ItemRequestMapper.toItemRequestDtoWithItems(request, availableItems);
    }
}

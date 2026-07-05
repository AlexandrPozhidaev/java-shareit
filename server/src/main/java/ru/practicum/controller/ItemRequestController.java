package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ItemRequestDto;
import ru.practicum.itemRequest.ItemRequestService;

import java.util.List;

@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;


    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequest(
            @RequestHeader("X-Sharer-User-Id") long requestorId,
            @RequestBody ItemRequestDto itemRequestDto) {
        ItemRequestDto savedRequest = itemRequestService.createRequest(itemRequestDto, requestorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRequest);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getUserRequests(
            @RequestHeader("X-Sharer-User-Id") long userId) {
        List<ItemRequestDto> requests = itemRequestService.getUserRequests(userId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllRequests() {
        List<ItemRequestDto> allRequests = itemRequestService.getAllRequests();
        return ResponseEntity.ok(allRequests);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getRequestById(@PathVariable long requestId) {
        ItemRequestDto request = itemRequestService.getRequestById(requestId);
        return ResponseEntity.ok(request);
    }
}

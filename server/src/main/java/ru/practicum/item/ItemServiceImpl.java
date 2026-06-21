package ru.practicum.item;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.booking.Booking;
import ru.practicum.booking.BookingRepository;
import ru.practicum.dto.BookingDto;
import ru.practicum.dto.BookingStatus;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.ItemDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.itemRequest.ItemRequest;
import ru.practicum.itemRequest.ItemRequestRepository;
import ru.practicum.mapper.BookingMapper;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.mapper.ItemMapper;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private CommentRepository commentRepository;
    private BookingRepository bookingRepository;
    private ItemRequestRepository itemRequestRepository;

    public ItemServiceImpl(UserRepository userRepository, ItemRepository itemRepository, CommentRepository commentRepository, BookingRepository bookingRepository, ItemRequestRepository itemRequestRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        }

        Item item = ItemMapper.toItem(itemDto, request);
        item.setOwner(owner);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        User owner = existingItem.getOwner();
        if (owner == null || !owner.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Только владелец может обновлять вещь с id: " + itemId);
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(existingItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));

        boolean isOwner = item.getOwner().getId().equals(userId);

        ItemDto itemDto = ItemMapper.toItemDto(item);

        List<Booking> bookings = bookingRepository.findByItemIdOrderByStartDesc(itemId);

        BookingDto lastBookingDto = null;
        BookingDto nextBookingDto = null;

        if (isOwner) {
            LocalDateTime now = LocalDateTime.now();

            List<Booking> completedBookings = bookings.stream()
                    .filter(booking -> booking.getEnd().isBefore(now))
                    .collect(Collectors.toList());

            if (!completedBookings.isEmpty()) {
                lastBookingDto = BookingMapper.toBookingDto(completedBookings.get(0));
            }

            List<Booking> futureBookings = bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(now))
                    .collect(Collectors.toList());

            if (!futureBookings.isEmpty()) {
                nextBookingDto = BookingMapper.toBookingDto(futureBookings.get(0));
            }
        }

        itemDto.setLastBooking(lastBookingDto);
        itemDto.setNextBooking(nextBookingDto);

        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId);
        List<CommentDto> commentDtos = comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemDto.setComments(commentDtos);

        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Item> items = itemRepository.findByOwnerId(userId);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String searchText = text.toLowerCase().trim();

        return itemRepository.findAll().stream()
                .filter(item -> item.getAvailable())
                .filter(item ->
                        item.getName().toLowerCase().contains(searchText) ||
                                (item.getDescription() != null &&
                                        item.getDescription().toLowerCase().contains(searchText))
                )
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, Long authorId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        LocalDateTime now = LocalDateTime.now();

        List<Booking> pastBookings = bookingRepository.findByBookerIdAndItemIdAndEndIsBeforeAndStatus(
                authorId,
                itemId,
                now,
                BookingStatus.APPROVED
        );

        if (pastBookings.isEmpty()) {
            throw new ValidationException("Пользователь не брал эту вещь в аренду или аренда не завершена");
        }

        Comment comment = CommentMapper.toComment(commentDto, author, item);
        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.toCommentDto(savedComment);
    }

}

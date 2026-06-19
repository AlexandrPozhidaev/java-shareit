package ru.practicum.booking;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.BookingDto;
import ru.practicum.dto.BookingStatus;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.Item;
import ru.practicum.item.ItemRepository;
import ru.practicum.mapper.BookingMapper;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              ItemRepository itemRepository,
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public BookingDto createBooking(BookingDto bookingDto, Long bookerId) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }

        List<Booking> existingBookings = bookingRepository
                .findByItemIdAndStartIsBeforeAndEndIsAfter(bookingDto.getItemId(),
                        bookingDto.getEnd(), bookingDto.getStart());
        if (!existingBookings.isEmpty()) {
            throw new ValidationException("Вещь уже забронирована на указанный период");
        }

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new ValidationException("Даты начала и конца бронирования обязательны");
        }
        if (!bookingDto.getStart().isBefore(bookingDto.getEnd())) {
            throw new ValidationException("Дата начала должна быть раньше даты окончания");
        }

        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(Long bookingId, Long ownerId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        Item item = booking.getItem();
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new ValidationException("Только владелец вещи может подтверждать бронирование");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Можно подтверждать только бронирования в статусе WAITING");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();

        if (!bookerId.equals(userId) && !ownerId.equals(userId)) {
            throw new ValidationException("Доступ к бронированию есть только у автора или владельца вещи");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings;
        switch (state) {
            case "CURRENT":
                bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(
                        userId, now, now);
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndIsBefore(userId, now);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartIsAfter(userId, now);
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED);
                break;
            default: // "ALL"
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, String state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings;
        switch (state) {
            case "CURRENT":
                bookings = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                        ownerId, now, now);
                break;
            case "PAST":
                bookings = bookingRepository.findByItemOwnerIdAndEndIsBefore(ownerId, now);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByItemOwnerIdAndStartIsAfter(ownerId, now);
                break;
            case "WAITING":
                bookings = bookingRepository.findByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED);
                break;
            default: // "ALL"
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
                break;
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}

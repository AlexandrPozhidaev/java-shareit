package ru.practicum.client;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.BookItemRequestDto;
import ru.practicum.dto.BookingDto;

@Service
@Slf4j
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(Long userId, BookItemRequestDto requestDto) {
        log.debug("Создание бронирования для пользователя ID: {}, запрос: {}", userId, requestDto);
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> approveBooking(Long ownerId, Long bookingId, boolean approved) {
        log.debug("Подтверждение бронирования ID: {} для владельца ID: {}, статус подтверждения: {}",
                bookingId, ownerId, approved);
        Map<String, Object> parameters = Map.of("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", ownerId, parameters, BookingDto.class);
    }

    public ResponseEntity<Object> getBookingById(Long userId, Long bookingId) {
        log.debug("Получение бронирования по ID: {} для пользователя ID: {}", bookingId, userId);
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getUserBookings(Long userId, String state, Integer from, Integer size) {
        log.debug("Получение бронирований пользователя ID: {}, состояние: {}, с позиции: {}, размер: {}",
                userId, state, from != null ? from : 0, size != null ? size : 10);
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from != null ? from : 0,
                "size", size != null ? size : 10
        );
        return get("", userId, parameters);
    }

    public ResponseEntity<Object> getOwnerBookings(Long ownerId, String state, Integer from, Integer size) {
        log.debug("Получение бронирований владельца ID: {}, состояние: {}, с позиции: {}, размер: {}",
                ownerId, state, from != null ? from : 0, size != null ? size : 10);
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from != null ? from : 0,
                "size", size != null ? size : 10
        );
        return get("/owner", ownerId, parameters);
    }
}

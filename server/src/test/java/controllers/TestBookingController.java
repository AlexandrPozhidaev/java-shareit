package controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.ShareItServerApplication;
import ru.practicum.controller.BookingController;
import ru.practicum.dto.BookingDto;
import ru.practicum.dto.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@ContextConfiguration(classes = ShareItServerApplication.class)
class TestBookingController extends TestControllerBase {

    @Test
    void createBooking_shouldReturnCreatedBooking() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto savedBooking = new BookingDto();
        savedBooking.setId(1L);
        savedBooking.setItemId(1L);

        when(bookingService.createBooking(bookingDto, 1L)).thenReturn(savedBooking);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.itemId").value(1));
    }

    @Test
    void approveBooking_shouldUpdateStatus() throws Exception {
        BookingDto updatedBooking = new BookingDto();
        updatedBooking.setId(1L);
        updatedBooking.setStatus(BookingStatus.APPROVED);

        when(bookingService.approveBooking(1L, 2L, true)).thenReturn(updatedBooking);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 2L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBookingById_shouldReturnBooking() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        when(bookingService.getBookingById(1L, 1L)).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getUserBookings_shouldReturnAllBookings() throws Exception {
        List<BookingDto> bookings = List.of(new BookingDto());
        bookings.get(0).setId(1L);
        when(bookingService.getUserBookings(1L, "ALL")).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getOwnerBookings_shouldReturnOwnersBookings() throws Exception {
        List<BookingDto> bookings = List.of(new BookingDto());
        bookings.get(0).setId(1L);
        when(bookingService.getOwnerBookings(1L, "ALL")).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}

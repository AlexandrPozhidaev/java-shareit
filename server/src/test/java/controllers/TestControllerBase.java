package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.booking.BookingService;
import ru.practicum.item.ItemService;
import ru.practicum.itemRequest.ItemRequestService;
import ru.practicum.user.UserService;

public class TestControllerBase {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected BookingService bookingService;

    @MockBean
    protected ItemService itemService;

    @MockBean
    protected UserService userService;

    @MockBean
    protected ItemRequestService itemRequestService;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
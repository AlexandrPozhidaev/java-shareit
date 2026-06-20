package controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.ShareItServerApplication;
import ru.practicum.controller.ItemController;
import ru.practicum.controller.ItemRequestController;
import ru.practicum.dto.ItemRequestDto;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@ContextConfiguration(classes = ShareItServerApplication.class)
public class TestItemRequestController extends TestControllerBase {

    @Test
    void createRequest_shouldReturnCreatedRequest() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Need a tool");

        ItemRequestDto savedRequest = new ItemRequestDto();
        savedRequest.setId(1L);
        savedRequest.setDescription("Need a tool");

        when(itemRequestService.createRequest(requestDto, 1L)).thenReturn(savedRequest);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getUserRequests_shouldReturnAllRequests() throws Exception {
        List<ItemRequestDto> requests = List.of(new ItemRequestDto());
        requests.get(0).setId(1L);
        requests.get(0).setDescription("Test request");
        when(itemRequestService.getUserRequests(1L)).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value("Test request"));
    }

    @Test
    void getAllRequests_shouldReturnAllRequests() throws Exception {
        List<ItemRequestDto> allRequests = List.of(new ItemRequestDto());
        allRequests.get(0).setId(1L);
        when(itemRequestService.getAllRequests()).thenReturn(allRequests);

        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getRequestById_shouldReturnRequest() throws Exception {
        ItemRequestDto request = new ItemRequestDto();
        request.setId(1L);
        request.setDescription("Sample request");
        when(itemRequestService.getRequestById(1L)).thenReturn(request);

        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Sample request"));
    }
}

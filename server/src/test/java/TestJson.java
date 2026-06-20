import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.ShareItServerApplication;
import ru.practicum.dto.BookingDto;
import ru.practicum.dto.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
@ContextConfiguration(classes = ShareItServerApplication.class)
public class TestJson {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void bookingDto_shouldSerializeCorrectly() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setItemId(10L);
        dto.setBookerId(20L);
        dto.setStart(LocalDateTime.of(2026, 6, 20, 10, 0));
        dto.setEnd(LocalDateTime.of(2026, 6, 25, 18, 0));
        dto.setStatus(BookingStatus.APPROVED);

        String jsonString = json.write(dto).getJson();

        assertThat(json.write(dto)).isEqualToJson("expected-booking-dto.json");

        Object status = JsonPath.read(jsonString, "$.status");
        Object id = JsonPath.read(jsonString, "$.id");
        Object itemId = JsonPath.read(jsonString, "$.itemId");
        Object start = JsonPath.read(jsonString, "$.start");

        assertThat(status).isEqualTo("APPROVED");
        assertThat(id).isEqualTo(1);
        assertThat(itemId).isEqualTo(10);
        assertThat(start).isEqualTo("2026-06-20T10:00:00");
    }

    @Test
    void bookingDto_shouldDeserializeCorrectly() throws Exception {
        String jsonContent = """
            {
                "id": 1,
                "itemId": 10,
                "bookerId": 20,
                "start": "2026-06-20T10:00:00",
                "end": "2026-06-25T18:00:00",
                "status": "APPROVED"
            }
        """;

        BookingDto result = json.parseObject(jsonContent);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getItemId()).isEqualTo(10L);
        assertThat(result.getBookerId()).isEqualTo(20L);
        assertThat(result.getStart())
                .isEqualTo(LocalDateTime.of(2026, 6, 20, 10, 0));
        assertThat(result.getEnd())
                .isEqualTo(LocalDateTime.of(2026, 6, 25, 18, 0));
        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }
}

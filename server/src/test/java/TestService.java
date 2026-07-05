

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ShareItServerApplication;
import ru.practicum.dto.ItemDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.Item;
import ru.practicum.item.ItemRepository;
import ru.practicum.item.ItemServiceImpl;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest(classes = ShareItServerApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class TestService {

        @Autowired
        private ItemServiceImpl itemService;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private ItemRepository itemRepository;

        private User owner;
        private Item item1;
        private Item item2;

        @BeforeEach
        void setUp() {
            owner = new User();
            owner.setName("Test User");
            owner.setEmail("test@example.com");
            owner = userRepository.save(owner);

            item1 = new Item();
            item1.setName("Item 1");
            item1.setDescription("Description 1");
            item1.setAvailable(true);
            item1.setOwner(owner);
            item1 = itemRepository.save(item1);

            item2 = new Item();
            item2.setName("Item 2");
            item2.setDescription("Description 2");
            item2.setAvailable(false);
            item2.setOwner(owner);
            item2 = itemRepository.save(item2);
        }

        @Test
        void getUserItems_shouldReturnAllItemsForUser() {

            List<ItemDto> result = itemService.getItemsByOwner(owner.getId());

            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(ItemDto::getName)
                    .containsExactlyInAnyOrder("Item 1", "Item 2");
        }

        @Test
        void getUserItems_withNonExistentUser_shouldThrowNotFoundException() {
            assertThatThrownBy(() -> itemService.getItemsByOwner(999L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("Пользователь не найден");
        }
    }

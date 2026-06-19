package ru.practicum;

import org.springframework.stereotype.Repository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long nextId = 1L;

    public Item create(Item item) {
        if (item.getId() == null) {
            item.setId(nextId++);
        }
        items.put(item.getId(), item);
        return item;
    }

    public Item update(Item item) {
        if (!items.containsKey(item.getId())) {
            throw new NotFoundException("Вещь с id " + item.getId() + " не найдена");
        }
        items.put(item.getId(), item);
        return item;
    }

    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    public List<Item> findByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner() != null &&
                        item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    public List<Item> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        String searchText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> {
                    String description = item.getDescription();
                    String name = item.getName();
                    return (description != null && description.toLowerCase().contains(searchText)) ||
                            (name != null && name.toLowerCase().contains(searchText));
                })
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        items.remove(id);
    }

    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }
}
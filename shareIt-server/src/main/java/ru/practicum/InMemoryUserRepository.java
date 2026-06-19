package ru.practicum;

import org.springframework.stereotype.Repository;
import ru.practicum.user.User;

import java.util.*;

@Repository
public class InMemoryUserRepository {
    public final Map<Long, User> users = new HashMap<>();
    private long nextId = 1L;

    public User create(User user) {
        if (user.getId() == null) {
            user.setId(nextId++);
        }
        users.put(user.getId(), user);
        return user;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public void delete(Long id) {
        users.remove(id);
    }

    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail() != null && user.getEmail().equals(email))
                .findFirst();
    }
}
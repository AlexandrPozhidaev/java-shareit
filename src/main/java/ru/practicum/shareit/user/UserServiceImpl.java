package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyUsedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final InMemoryUserRepository userRepository;

    public UserServiceImpl(InMemoryUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException("Пользователь с электронной почтой " + user.getEmail() + " уже существует");
        }
        return userRepository.create(user);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + user.getId() + " не найден"));

        if (existingUser.getEmail() != null && !existingUser.getEmail().equals(existingUser.getEmail())) {
            return userRepository.create(user);
        }

        Optional<User> userWithNewEmail = userRepository.findByEmail(user.getEmail());

        if (userWithNewEmail.isPresent()) {
            User conflictUser = userWithNewEmail.get();
            if (!conflictUser.getId().equals(user.getId())) {
                throw new EmailAlreadyUsedException(
                        "Электронная почта " + user.getEmail() + " уже используется пользователем с ID " + conflictUser.getId()
                );
            }
        }

        return userRepository.create(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        userRepository.delete(userId);
    }
}

package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailAlreadyUsedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException("Пользователь с электронной почтой " + user.getEmail() + " уже существует");
        }
        return userRepository.save(user);
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

    public User updateUser(User user) {
        try {
            User existingUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new NotFoundException("Пользователь с ID " + user.getId() + " не найден"));

            if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
                Optional<User> userWithNewEmail = userRepository.findByEmail(user.getEmail());
                if (userWithNewEmail.isPresent()) {
                    User conflictUser = userWithNewEmail.get();
                    if (!conflictUser.getId().equals(user.getId())) {
                        throw new EmailAlreadyUsedException(
                                "Электронная почта " + user.getEmail() + " уже используется пользователем с ID " + conflictUser.getId()
                        );
                    }
                }
            }

            existingUser.setName(user.getName() != null ? user.getName() : existingUser.getName());
            existingUser.setEmail(user.getEmail() != null ? user.getEmail() : existingUser.getEmail());

            return userRepository.save(existingUser);
        } catch (Exception e) {
            log.error("Ошибка при обновлении пользователя с ID: {}", user.getId(), e);
            throw e;
        }
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        userRepository.deleteById(userId);
    }
}

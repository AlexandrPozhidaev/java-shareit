package ru.practicum.controller;

import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.UserDto;
import ru.practicum.mapper.UserMapper;
import ru.practicum.user.User;
import ru.practicum.user.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createUser(@Validated(UserDto.OnCreate.class) @RequestBody UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User savedUser = userService.createUser(user);
        return UserMapper.toUserDto(savedUser);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable @Positive Long userId) {
        User user = userService.getUserById(userId);
        return UserMapper.toUserDto(user);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable @Positive Long userId,
                              @Validated(UserDto.OnUpdate.class) @RequestBody UserDto userDto) {
        userDto.setId(userId);
        User user = UserMapper.toUser(userDto);
        User updatedUser = userService.updateUser(user);
        return UserMapper.toUserDto(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable @Positive Long userId) {
        userService.deleteUser(userId);
    }
}
package ru.practicum.controller;

import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.UserClient;
import ru.practicum.dto.UserDto;

@RestController
@RequestMapping(path = "/users")
public class UserGatewayController {
    private final UserClient userClient;

    public UserGatewayController(UserClient userClient) {
        this.userClient = userClient;
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated(UserDto.OnCreate.class) @RequestBody UserDto userDto) {
        return userClient.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public  ResponseEntity<Object> getUserById(@PathVariable @Positive Long userId) {
        return userClient.getUserById(userId);
    }

    @GetMapping
    public  ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }

    @PatchMapping("/{userId}")
    public  ResponseEntity<Object> updateUser(@PathVariable @Positive Long userId,
                              @Validated(UserDto.OnUpdate.class) @RequestBody UserDto userDto) {
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable @Positive Long userId) {
        return userClient.deleteUser(userId);
    }
}
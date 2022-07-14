package ru.yandex.practikum.filmorate.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practikum.filmorate.model.user.User;
import ru.yandex.practikum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {


    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Обработка эндпойнта GET /users");
        return new ResponseEntity<>(userService.getAllUsersFromStorage(), HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable long userId) {
        log.info("Обработка эндпойнта GET /users/{}", userId);
        return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        log.info("Обработка эндпойнта POST /users");
        return new ResponseEntity<>(userService.addUserToStorage(user), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        log.info("Обработка эндпойнта PUT /users");
        return new ResponseEntity<>(userService.updateUserInStorage(user), HttpStatus.OK);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable long userId, @PathVariable long friendId) {
        log.info("Обработка эндпойнта PUT /users/{}/friends/{}", userId, friendId);
        return new ResponseEntity<>(userService.addFriendToUser(userId, friendId), HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<User> removeFriend(@PathVariable long userId, @PathVariable long friendId) {
        log.info("Обработка эндпойнта DELETE /users/{}/friends/{}", userId, friendId);
        return new ResponseEntity<>(userService.removeFriendFromUser(userId, friendId), HttpStatus.OK);
    }

    @GetMapping("/{userId}/friends")
    public ResponseEntity<List<User>> getFriends(@PathVariable long userId) {
        log.info("Обработка эндпойнта GET /users/{}/friends", userId);
        return new ResponseEntity<>(new ArrayList<>(userService.getFriendsByUserId(userId)), HttpStatus.OK);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public ResponseEntity<List<User>> getCommonFriends(@PathVariable long userId, @PathVariable long otherId) {
        log.info("Обработка эндпойнта GET /users/{}/friends/common/{}", userId, otherId);
        return new ResponseEntity<>(userService.getCommonFriends(userId, otherId), HttpStatus.OK);
    }

}

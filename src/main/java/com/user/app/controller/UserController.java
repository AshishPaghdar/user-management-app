package com.user.app.controller;

import com.user.app.entity.User;
import com.user.app.exception.ResourceNotFoundException;
import com.user.app.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    /**
     * Creates a new user.
     *
     * @param user the user to create
     * @return a {@link ResponseEntity} containing the created {@link User} and HTTP status CREATED
     * @throws Exception if the user creation fails
     */
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        log.info("Creating user: {}", user);
        try {
            User createUser = userService.createUser(user);
            log.info("User Created Successfully: {}", createUser);
            return new ResponseEntity<>(createUser, HttpStatus.CREATED);
        } catch (Exception ex) {
            log.error("Failed to create user" + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Retrieves all users.
     *
     * @return a {@link ResponseEntity} containing a list of {@link User} objects and HTTP status OK
     * @throws Exception if an error occurs while retrieving the users
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Getting all users");
        List<User> users = null;
        try {
            users = userService.getAllUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Exception: while retrieving users' list." + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user to retrieve
     * @return a {@link ResponseEntity} containing the found {@link User} and HTTP status OK,
     * or HTTP status NOT FOUND if the user is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long userId) {
        log.info("Getting user with id: {}", userId);
        try {
            Optional<User> optionalUser = userService.findById(userId);
            User user = optionalUser.orElse(new User());
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            log.error("User not found with id: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageSource.getMessage("user.not.found.msg", null, Locale.ENGLISH) + " " + userId);
        }
    }

    /**
     * Updates an existing user by their ID.
     *
     * @param userId the ID of the user to update
     * @param user   the new user data
     * @return a {@link ResponseEntity} containing the updated {@link User} and HTTP status OK,
     * or HTTP status NOT FOUND if the user is not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long userId, @Valid @RequestBody User user) {
        log.info("Updating user: {}", user);
        try {
            User updatedUser = userService.updateUser(user, userId);
            log.info("User Updated Successfully: {}", updatedUser);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            log.error("User not found with id: {}", user.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageSource.getMessage("user.not.found.msg", null, Locale.ENGLISH) + " " + userId);
        }
    }

    /**
     * Deletes a user by their ID.
     *
     * @param userId the ID of the user to delete
     * @return a {@link ResponseEntity} with HTTP status OK if the deletion was successful,
     * or HTTP status NOT FOUND if the user is not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long userId) {
        log.info("Deleting user with id: {}", userId);
        try {
            userService.deleteUser(userId);
            log.info("User deleted successfully");
            return ResponseEntity.status(HttpStatus.OK).body(messageSource.getMessage("user.deleted.successfully.msg", null, Locale.ENGLISH) + " " + userId);
        } catch (ResourceNotFoundException ex) {
            log.error("User not found with id: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageSource.getMessage("user.not.found.msg", null, Locale.ENGLISH) + " " + userId);
        }
    }
}

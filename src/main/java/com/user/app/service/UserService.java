package com.user.app.service;

import com.user.app.entity.User;
import com.user.app.exception.ResourceNotFoundException;
import com.user.app.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Locale;
import java.util.Optional;


@Validated
@Service
@AllArgsConstructor
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private MessageSource messageSource;

    /**
     * Creates a new user and publishes a creation event.
     *
     * @param user the user entity to be created
     * @return the created {@link User}
     */
    public User createUser(@Valid User user) {
        User savedUser = userRepository.save(user);
        kafkaTemplate.send("user-events", "User created: " + savedUser.getId());
        return savedUser;
    }

    /**
     * Retrieves all users from the repository.
     *
     * @return a list of all {@link User} entities
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Finds a user by their ID.
     *
     * @param userId the ID of the user to retrieve
     * @return an {@link Optional} containing the found {@link User}, or empty if not found
     * @throws ResourceNotFoundException if no user is found with the given ID
     */
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("user.not.found.msg", null, Locale.ENGLISH))));
    }

    /**
     * Updates an existing user by their ID and publishes an update event.
     *
     * @param user   the user entity containing updated information
     * @param userId the ID of the user to update
     * @return the updated {@link User}
     * @throws ResourceNotFoundException if no user is found with the given ID
     */
    public User updateUser(User user, Long userId) {
        User validUser = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("user.not.found.msg", null, Locale.ENGLISH)));
        validUser.setName(user.getName());
        validUser.setMobile(user.getMobile());
        validUser.setEmail(user.getEmail());
        validUser.setAddress(user.getAddress());
        User updatedUser = userRepository.save(validUser);
        kafkaTemplate.send("user-events", "User updated: " + updatedUser.getId());
        return updatedUser;
    }

    /**
     * Deletes a user by their ID and publishes a deletion event.
     *
     * @param userId the ID of the user to delete
     * @throws ResourceNotFoundException if no user is found with the given ID
     */
    public void deleteUser(Long userId) {
        User validUser = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("user.not.found.msg", null, Locale.ENGLISH)));
        userRepository.deleteById(userId);
        kafkaTemplate.send("user-events", "User deleted: " + userId);
    }
}
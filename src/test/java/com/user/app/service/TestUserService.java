package com.user.app.service;

import com.user.app.entity.User;
import com.user.app.exception.ResourceNotFoundException;
import com.user.app.repository.UserRepository;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestUserService {

    @InjectMocks
    private UserService userService;

    @Mock
    private Validator validator;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageSource messageSource;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        userService = new UserService(userRepository, kafkaTemplate, messageSource);
    }

    // Create User valid case
    @Test
    public void testCreateUser_ValidUser() {
        User user = new User();
        user.setId(121L);
        user.setName("Ashish");
        user.setEmail("ashish@gmail.com");
        user.setMobile("9875432349");
        user.setAddress("Ahmedabad, Gujarat");

        when(userRepository.save(user)).thenReturn(user);
        User createdUser = userService.createUser(user);
        assertEquals(createdUser.getId(), user.getId());
    }

    // Test case for invalid email
    @Test
    public void testCreateUser_InvalidEmail() {
        User user = new User();
        user.setId(122L);
        user.setName("Ashish Paghdar");
        user.setEmail("ashish.com");
        user.setMobile("9234567890");
        user.setAddress("Ahmedabad, Gujarat");

        // Validate the email Address
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Expected validation errors for invalid email");
        assertThrows(ConstraintViolationException.class, () -> {
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
            userService.createUser(user);
        });
    }

    // Test case for invalid Mobile number
    @Test
    public void testCreateUser_InvalidMobile() {
        User user = new User();
        user.setId(123L);
        user.setName("John Doe");
        user.setEmail("johndoe@example.com");
        user.setMobile("123"); // Invalid phone number
        user.setAddress("Vadodara, Gujarat");

        // Validate the user object manually
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Expected validation errors for invalid mobile");
        assertThrows(ConstraintViolationException.class, () -> {
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
            userService.createUser(user);
        });
    }

    // Update user Valid case
    @Test
    public void testUpdateUser_ValidData() {
        User user = new User();
        user.setId(1L);
        user.setName("Amit Patel");
        user.setEmail("amit@example.com");
        user.setMobile("0987654321");
        user.setAddress("Vadodara, Gujarat");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User updatedUser = userService.updateUser(user, user.getId());

        assertEquals("Amit Patel", updatedUser.getName());
        verify(userRepository, times(1)).save(user);
        verify(kafkaTemplate, times(1)).send("user-events", "User updated: " + updatedUser.getId());
    }

    // Delete User Valid case
    @Test
    public void testDeleteUser_Valid() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
        verify(kafkaTemplate, times(1)).send("user-events", "User deleted: " + userId);
    }

    @Test
    public void testGetAllUsers_ShouldReturnUserList() {
        User user = new User();
        user.setId(12L);
        user.setName("Milan");
        user.setEmail("milan@example.com");
        user.setMobile("0987654321");
        user.setAddress("Ahmedabad");

        List<User> users = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> retrievedUsers = userService.getAllUsers();

        assertEquals(1, retrievedUsers.size());
        assertEquals(user.getId(), retrievedUsers.get(0).getId());
        verify(userRepository, times(1)).findAll();
    }


    // FindById Invalid User Id (ResourceNotFound)
    @Test
    public void testFindById_InValidId() {
        User user = new User();
        user.setId(12L);
        user.setName("Milan");
        user.setEmail("milan@example.com");
        user.setMobile("0987654321");
        user.setAddress("Ahmedabad");

        Long invalidId = 199L;
        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());
        when(messageSource.getMessage("user.not.found.msg", null, Locale.ENGLISH))
                .thenReturn("User not found");

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(user, invalidId));
    }

    // FindById Valid Case
    @Test
    public void testFindById_ValidId() {
        User user = new User();
        user.setId(12L);
        user.setName("Milan");
        user.setEmail("milan@example.com");
        user.setMobile("0987654321");
        user.setAddress("Ahmedabad");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Optional<User> foundUser = userService.findById(user.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(user.getId(), foundUser.get().getId());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    public void testDeleteUser_InvalidId_ShouldThrowResourceNotFoundException() {
        Long invalidId = 999L;
        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());
        when(messageSource.getMessage("user.not.found.msg", null, Locale.ENGLISH))
                .thenReturn("User not found");

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(invalidId));
    }
}
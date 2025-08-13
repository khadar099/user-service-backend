package com.yourcompany.ecommerce.userservice.service;

import com.yourcompany.ecommerce.userservice.entity.User;
import com.yourcompany.ecommerce.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegister() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("plainpassword");

        when(passwordEncoder.encode("plainpassword")).thenReturn("hashedpassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User savedUser = userService.register(user);

        assertNotNull(savedUser);
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("hashedpassword", savedUser.getPassword());

        verify(passwordEncoder).encode("plainpassword");
        verify(userRepository).save(savedUser);
    }

    @Test
    public void testLogin_Success() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("hashedpassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plainpassword", "hashedpassword")).thenReturn(true);

        User loggedInUser = userService.login("testuser", "plainpassword");

        assertEquals("testuser", loggedInUser.getUsername());
    }

    @Test
    public void testLogin_UserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.login("unknown", "anyPassword");
        });
    }

    @Test
    public void testLogin_InvalidPassword() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("hashedpassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "hashedpassword")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> {
            userService.login("testuser", "wrongpassword");
        });
    }
}

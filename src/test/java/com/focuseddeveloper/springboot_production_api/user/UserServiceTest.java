package com.focuseddeveloper.springboot_production_api.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repo;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserService userService;

    private final String email = "test@example.com";
    private final String rawPassword = "password";
    private final String encodedPassword = "encodedPassword";
    private final String fullName = "Test User";

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRoles("ROLE_STUDENT");
        user.setFullName(fullName);
    }

    @Test
    void registerStudent_success() {
        when(repo.existsByEmail(email)).thenReturn(false);
        when(encoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(repo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.registerStudent(email, rawPassword, fullName);

        assertEquals(email, result.getEmail());
        assertEquals(encodedPassword, result.getPassword());
        assertEquals("ROLE_STUDENT", result.getRoles());
        assertEquals(fullName, result.getFullName());
        verify(repo).save(any(User.class));
    }

    @Test
    void registerStudent_emailAlreadyExists_throwsException() {
        when(repo.existsByEmail(email)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                userService.registerStudent(email, rawPassword, fullName));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Email already in use", ex.getReason());
        verify(repo, never()).save(any());
    }

    @Test
    void authenticate_success() {
        when(repo.findByEmail(email)).thenReturn(Optional.of(user));
        when(encoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        User result = userService.authenticate(email, rawPassword);

        assertEquals(user, result);
    }

    @Test
    void authenticate_userNotFound_throwsException() {
        when(repo.findByEmail(email)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                userService.authenticate(email, rawPassword));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        assertEquals("Invalid credentials", ex.getReason());
    }

    @Test
    void authenticate_passwordMismatch_throwsException() {
        when(repo.findByEmail(email)).thenReturn(Optional.of(user));
        when(encoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                userService.authenticate(email, rawPassword));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        assertEquals("Invalid credentials", ex.getReason());
    }
}
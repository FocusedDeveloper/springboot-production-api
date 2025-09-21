package com.focuseddeveloper.springboot_production_api.auth;

import com.focuseddeveloper.springboot_production_api.user.User;
import com.focuseddeveloper.springboot_production_api.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_ShouldReturnCreatedTokenRes() {
        // Arrange
        String email = "test@example.com";
        String password = "password";
        String fullName = "Test User";
        User user = mock(User.class);
        UUID userId = UUID.randomUUID();
        when(user.getId()).thenReturn(userId);
        when(user.getEmail()).thenReturn(email);
        when(userService.registerStudent(email, password, fullName)).thenReturn(user);
        when(jwtService.generate(userId, email, List.of("ROLE_STUDENT"))).thenReturn("token123");

        AuthController.RegisterReq req = new AuthController.RegisterReq(email, password, fullName);

        // Act
        ResponseEntity<AuthController.TokenRes> res = authController.register(req);

        // Assert
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        assertEquals("token123", res.getBody().accessToken());
        verify(userService).registerStudent(email, password, fullName);
        verify(jwtService).generate(userId, email, List.of("ROLE_STUDENT"));
    }

    @Test
    void login_ShouldReturnTokenRes() {
        // Arrange
        String email = "test@example.com";
        String password = "password";
        User user = mock(User.class);
        UUID userId = UUID.randomUUID();
        when(user.getId()).thenReturn(userId);
        when(user.getEmail()).thenReturn(email);
        when(user.getRoleList()).thenReturn(List.of("ROLE_STUDENT", "ROLE_USER"));
        when(userService.authenticate(email, password)).thenReturn(user);
        when(jwtService.generate(userId, email, List.of("ROLE_STUDENT", "ROLE_USER"))).thenReturn("token456");

        AuthController.LoginReq req = new AuthController.LoginReq(email, password);

        // Act
        AuthController.TokenRes res = authController.login(req);

        // Assert
        assertEquals("token456", res.accessToken());
        verify(userService).authenticate(email, password);
        verify(jwtService).generate(userId, email, List.of("ROLE_STUDENT", "ROLE_USER"));
    }
}

package com.focuseddeveloper.springboot_production_api.me;

import com.focuseddeveloper.springboot_production_api.auth.CurrentUserHelper;
import com.focuseddeveloper.springboot_production_api.user.User;
import com.focuseddeveloper.springboot_production_api.user.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeControllerTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    MeController meController;

    @Test
    void whoAmI_returnsCurrentUserInfo_whenUserExists() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        List<String> roles = List.of("ROLE_USER", "ROLE_ADMIN");
        String fullName = "Test User";

        // Mock static methods of CurrentUserHelper
        try (MockedStatic<CurrentUserHelper> mocked = mockStatic(CurrentUserHelper.class)) {
            mocked.when(CurrentUserHelper::getCurrentUserId).thenReturn(userId);
            mocked.when(CurrentUserHelper::getCurrentEmail).thenReturn(email);
            mocked.when(CurrentUserHelper::getCurrentRoles).thenReturn(roles);

            User user = mock(User.class);
            when(user.getFullName()).thenReturn(fullName);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            MeController.WhoAmI result = meController.whoAmI();

            assertEquals(userId, result.id());
            assertEquals(email, result.email());
            assertArrayEquals(roles.toArray(new String[0]), result.roles());
            assertEquals(fullName, result.fullName());
        }
    }

    @Test
    void whoAmI_throwsUnauthorized_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        List<String> roles = List.of("ROLE_USER");

        try (MockedStatic<CurrentUserHelper> mocked = mockStatic(CurrentUserHelper.class)) {
            mocked.when(CurrentUserHelper::getCurrentUserId).thenReturn(userId);
            mocked.when(CurrentUserHelper::getCurrentEmail).thenReturn(email);
            mocked.when(CurrentUserHelper::getCurrentRoles).thenReturn(roles);

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> meController.whoAmI());
            assertEquals(401, ex.getStatusCode().value());
            assertTrue(ex.getReason().contains("User not found"));
        }
    }
}
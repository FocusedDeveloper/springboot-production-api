package com.focuseddeveloper.springboot_production_api.auth;

import com.focuseddeveloper.springboot_production_api.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private JwtProperties jwtProperties;

    private final String secret = "my-very-secret-key-that-is-long-enough-for-hmac-sha";
    private final String issuer = "test-issuer";
    private final int accessTokenMinutes = 10;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties() {
            @Override
            public String getSecret() {
                return secret;
            }

            @Override
            public String getIssuer() {
                return issuer;
            }

            @Override
            public long getAccessTokenMinutes() {
                return accessTokenMinutes;
            }

        };
        jwtService = new JwtService(jwtProperties);
    }

    @Test
    void generateAndVerifyToken_shouldReturnCorrectClaims() {
        UUID userId = UUID.randomUUID();
        String email = "user@example.com";
        List<String> roles = Arrays.asList("ROLE_STUDENT", "ROLE_TEACHER");

        String token = jwtService.generate(userId, email, roles);
        assertNotNull(token);

        Claims claims = jwtService.verify(token);

        assertEquals(email, claims.getSubject());
        assertEquals(userId.toString(), claims.get("uid"));
        assertEquals(issuer, claims.getIssuer());
        assertTrue(claims.get("roles") instanceof Collection);
        Collection<?> rolesClaim = (Collection<?>) claims.get("roles");
        assertTrue(rolesClaim.containsAll(roles));
    }

    @Test
    void verify_withInvalidToken_shouldThrowException() {
        String invalidToken = "invalid.token.value";
        assertThrows(JwtException.class, () -> jwtService.verify(invalidToken));
    }

    @Test
    void verify_withWrongIssuer_shouldThrowException() {
        UUID userId = UUID.randomUUID();
        String email = "user@example.com";
        List<String> roles = Collections.singletonList("USER");

        // Create a JwtService with a different issuer
        JwtProperties wrongIssuerProps = new JwtProperties() {
            @Override
            public String getSecret() {
                return secret;
            }

            @Override
            public String getIssuer() {
                return "wrong-issuer";
            }

        };
        JwtService wrongIssuerService = new JwtService(wrongIssuerProps);

        String token = wrongIssuerService.generate(userId, email, roles);

        assertThrows(JwtException.class, () -> jwtService.verify(token));
    }
}
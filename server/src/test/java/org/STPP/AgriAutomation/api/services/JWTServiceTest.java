package org.STPP.AgriAutomation.api.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import org.STPP.AgriAutomation.data.entities.Role;
import org.STPP.AgriAutomation.data.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JWTServiceTest {

    private JWTService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JWTService();
        String secret = Base64.getEncoder()
                .encodeToString("01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8));
        ReflectionTestUtils.setField(jwtService, "secretKey", secret);
        ReflectionTestUtils.setField(jwtService, "jwtAccessExpirationMs", 60_000L);
        ReflectionTestUtils.setField(jwtService, "jwtRefreshExpirationMs", 60_000L);
    }

    @Test
    void generateAccessTokenProducesValidTokenWithUsername() {
        User user = user(10L, "alice", Role.USER);

        String token = jwtService.generateAccessToken(user);

        assertThat(jwtService.validateToken(token)).isTrue();
        assertThat(jwtService.extractUsername(token)).isEqualTo("alice");
    }

    @Test
    void generateRefreshTokenProducesValidTokenWithUsernameAndSessionId() {
        UUID sessionId = UUID.randomUUID();

        String token = jwtService.generateRefreshToken("alice", sessionId);

        assertThat(jwtService.validateToken(token)).isTrue();
        assertThat(jwtService.extractUsername(token)).isEqualTo("alice");
        assertThat(jwtService.extractSessionId(token)).isEqualTo(sessionId);
    }

    @Test
    void validateTokenReturnsFalseForMalformedToken() {
        assertThat(jwtService.validateToken("not-a-real-jwt")).isFalse();
    }

    @Test
    void validateTokenReturnsFalseForExpiredToken() {
        ReflectionTestUtils.setField(jwtService, "jwtAccessExpirationMs", -1_000L);
        User user = user(10L, "alice", Role.USER);

        String expiredToken = jwtService.generateAccessToken(user);

        assertThat(jwtService.validateToken(expiredToken)).isFalse();
    }

    private static User user(Long id, String username, String roleName) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.addRole(new Role(roleName));
        return user;
    }
}

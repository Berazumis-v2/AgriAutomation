package org.STPP.AgriAutomation.api.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.STPP.AgriAutomation.api.repositories.RoleRepository;
import org.STPP.AgriAutomation.api.repositories.UserRepo;
import org.STPP.AgriAutomation.data.dtos.auth.AuthResponse;
import org.STPP.AgriAutomation.data.dtos.auth.LoginRequest;
import org.STPP.AgriAutomation.data.dtos.auth.RegisterRequest;
import org.STPP.AgriAutomation.data.entities.Role;
import org.STPP.AgriAutomation.data.entities.Session;
import org.STPP.AgriAutomation.data.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private JWTService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepo userRepo;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private SessionService sessionService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

    @Test
    void registerEncodesPasswordAssignsUserRoleAndSaves() {
        RegisterRequest request = registerRequest("alice", "plain-password");
        Role role = new Role(Role.USER);
        when(userRepo.findByUsername("alice")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        when(roleRepository.findByName(Role.USER)).thenReturn(Optional.of(role));
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.register(request);

        assertThat(saved.getUsername()).isEqualTo("alice");
        assertThat(saved.getPassword()).isEqualTo("encoded-password");
        assertThat(saved.getRoles()).containsExactly(role);
        verify(userRepo).save(saved);
    }

    @Test
    void registerRejectsDuplicateUsername() {
        RegisterRequest request = registerRequest("alice", "plain-password");
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Username already taken");

        verify(userRepo, never()).save(any());
    }

    @Test
    void loginAuthenticatesCreatesSessionAndReturnsTokens() {
        LoginRequest request = loginRequest("alice", "plain-password");
        User user = user(9L, "alice");
        UUID sessionId = UUID.randomUUID();
        Session session = new Session(user.getId().toString(), Instant.now().plusSeconds(60));
        session.setSessionId(sessionId);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(sessionService.createSession(eq("9"), any(Instant.class))).thenReturn(session);
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken("alice", sessionId)).thenReturn("refresh-token");

        AuthResponse response = userService.login(request);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        verify(sessionService).createSession(eq("9"), any(Instant.class));
    }

    @Test
    void loginConvertsBadCredentialsToRuntimeException() {
        LoginRequest request = loginRequest("alice", "wrong-password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad credentials"));

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    void refreshAccessTokenValidatesSessionExtendsItAndReturnsNewTokens() {
        String refreshToken = "refresh-token";
        UUID sessionId = UUID.randomUUID();
        User user = user(9L, "alice");
        when(jwtService.validateToken(refreshToken)).thenReturn(true);
        when(jwtService.extractUsername(refreshToken)).thenReturn("alice");
        when(jwtService.extractSessionId(refreshToken)).thenReturn(sessionId);
        when(sessionService.isSessionValid(sessionId)).thenReturn(true);
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken("alice", sessionId)).thenReturn("new-refresh-token");

        AuthResponse response = userService.refreshAccessToken(refreshToken);

        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
        verify(sessionService).extendSession(eq(sessionId), any(Instant.class));
    }

    @Test
    void refreshAccessTokenRejectsInvalidTokenBeforeReadingClaims() {
        when(jwtService.validateToken("bad-token")).thenReturn(false);

        assertThatThrownBy(() -> userService.refreshAccessToken("bad-token"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid refresh token");

        verify(jwtService, never()).extractUsername(any());
        verify(sessionService, never()).extendSession(any(), any());
    }

    @Test
    void refreshAccessTokenRejectsInvalidSession() {
        String refreshToken = "refresh-token";
        UUID sessionId = UUID.randomUUID();
        when(jwtService.validateToken(refreshToken)).thenReturn(true);
        when(jwtService.extractUsername(refreshToken)).thenReturn("alice");
        when(jwtService.extractSessionId(refreshToken)).thenReturn(sessionId);
        when(sessionService.isSessionValid(sessionId)).thenReturn(false);

        assertThatThrownBy(() -> userService.refreshAccessToken(refreshToken))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Session is invalid or expired");

        verify(userRepo, never()).findByUsername(any());
    }

    @Test
    void logoutValidatesRefreshTokenAndRevokesSession() {
        String refreshToken = "refresh-token";
        UUID sessionId = UUID.randomUUID();
        when(jwtService.validateToken(refreshToken)).thenReturn(true);
        when(jwtService.extractSessionId(refreshToken)).thenReturn(sessionId);

        userService.logout(refreshToken);

        verify(sessionService).revokeSession(sessionId);
    }

    @Test
    void logoutRejectsInvalidToken() {
        when(jwtService.validateToken("bad-token")).thenReturn(false);

        assertThatThrownBy(() -> userService.logout("bad-token"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid refresh token");

        verify(sessionService, never()).revokeSession(any());
    }

    private static RegisterRequest registerRequest(String username, String password) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }

    private static LoginRequest loginRequest(String username, String password) {
        LoginRequest request = new LoginRequest();
        ReflectionTestUtils.setField(request, "username", username);
        ReflectionTestUtils.setField(request, "password", password);
        return request;
    }

    private static User user(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }
}

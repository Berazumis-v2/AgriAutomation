package org.STPP.AgriAutomation.api.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.STPP.AgriAutomation.api.services.UserService;
import org.STPP.AgriAutomation.data.dtos.UserDTO;
import org.STPP.AgriAutomation.data.dtos.auth.AuthResponse;
import org.STPP.AgriAutomation.data.dtos.auth.LoginRequest;
import org.STPP.AgriAutomation.data.dtos.auth.RegisterRequest;
import org.STPP.AgriAutomation.data.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    private UserController controller;

    @BeforeEach
    void setUp() {
        controller = new UserController();
        ReflectionTestUtils.setField(controller, "service", userService);
    }

    @Test
    void registerReturnsCreatedUserDto() {
        RegisterRequest request = registerRequest("alice", "password");
        User savedUser = new User();
        savedUser.setUsername("alice");
        when(userService.register(request)).thenReturn(savedUser);

        ResponseEntity<UserDTO> response = controller.register(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getUsername()).isEqualTo("alice");
    }

    @Test
    void loginSetsRefreshTokenCookieAndHidesRefreshTokenInBody() {
        LoginRequest request = loginRequest("alice", "password");
        when(userService.login(request)).thenReturn(new AuthResponse("access-token", "refresh-token"));

        ResponseEntity<AuthResponse> response = controller.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst("Set-Cookie"))
                .contains("refreshToken=refresh-token")
                .contains("HttpOnly")
                .contains("Max-Age=259200");
        assertThat(response.getBody().getAccessToken()).isEqualTo("access-token");
        assertThat(response.getBody().getRefreshToken()).isEqualTo("Cookie set");
    }

    @Test
    void refreshTokenReturnsUnauthorizedWhenCookieMissing() {
        ResponseEntity<AuthResponse> response = controller.refreshToken(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(userService, never()).refreshAccessToken(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void refreshTokenReturnsUnauthorizedWhenServiceRejectsToken() {
        when(userService.refreshAccessToken("bad-refresh")).thenThrow(new RuntimeException("invalid"));

        ResponseEntity<AuthResponse> response = controller.refreshToken("bad-refresh");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void refreshTokenSetsNewCookieWhenServiceReturnsTokens() {
        when(userService.refreshAccessToken("old-refresh"))
                .thenReturn(new AuthResponse("new-access", "new-refresh"));

        ResponseEntity<AuthResponse> response = controller.refreshToken("old-refresh");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst("Set-Cookie")).contains("refreshToken=new-refresh");
        assertThat(response.getBody().getAccessToken()).isEqualTo("new-access");
        assertThat(response.getBody().getRefreshToken()).isEqualTo("new-refresh");
    }

    @Test
    void logoutRevokesTokenWhenCookiePresentAndClearsCookie() {
        ResponseEntity<?> response = controller.logout("refresh-token");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Logged out successfully");
        assertThat(response.getHeaders().getFirst("Set-Cookie"))
                .contains("refreshToken=")
                .contains("Max-Age=0");
        verify(userService).logout("refresh-token");
    }

    @Test
    void checkRefreshTokenReturnsFalseWhenCookieMissing() {
        ResponseEntity<Boolean> response = controller.checkRefreshToken("");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isFalse();
    }

    @Test
    void checkRefreshTokenRefreshesValidTokenAndReturnsTrue() {
        when(userService.refreshAccessToken("refresh-token"))
                .thenReturn(new AuthResponse("access-token", "new-refresh"));

        ResponseEntity<Boolean> response = controller.checkRefreshToken("refresh-token");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isTrue();
        assertThat(response.getHeaders().getFirst("Set-Cookie")).contains("refreshToken=new-refresh");
        verify(userService).validateRefreshToken("refresh-token");
    }

    @Test
    void validateTokenStripsBearerPrefixBeforeDelegating() {
        ResponseEntity<Boolean> response = controller.validateToken("Bearer access-token");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isTrue();
        verify(userService).validateAccessToken("access-token");
    }

    @Test
    void validateTokenReturnsFalseForMalformedHeader() {
        ResponseEntity<Boolean> response = controller.validateToken("bad");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isFalse();
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
}

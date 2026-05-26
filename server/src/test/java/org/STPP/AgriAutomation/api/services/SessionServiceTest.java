package org.STPP.AgriAutomation.api.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.STPP.AgriAutomation.api.repositories.SessionRepository;
import org.STPP.AgriAutomation.data.entities.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private SessionService sessionService;

    @Test
    void createSessionSavesNewNonRevokedSession() {
        Instant expiresAt = Instant.now().plusSeconds(60);
        Session savedSession = new Session("42", expiresAt);
        UUID sessionId = UUID.randomUUID();
        savedSession.setSessionId(sessionId);
        when(sessionRepository.save(org.mockito.ArgumentMatchers.any(Session.class))).thenReturn(savedSession);

        Session result = sessionService.createSession("42", expiresAt);

        assertThat(result.getSessionId()).isEqualTo(sessionId);
        assertThat(result.getUserId()).isEqualTo("42");
        assertThat(result.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(result.isRevoked()).isFalse();
    }

    @Test
    void isSessionValidReturnsTrueForExistingUnrevokedFutureSession() {
        UUID sessionId = UUID.randomUUID();
        Session session = new Session("42", Instant.now().plusSeconds(60));
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        boolean valid = sessionService.isSessionValid(sessionId);

        assertThat(valid).isTrue();
    }

    @Test
    void isSessionValidReturnsFalseForExpiredRevokedOrMissingSession() {
        UUID expiredId = UUID.randomUUID();
        UUID revokedId = UUID.randomUUID();
        UUID missingId = UUID.randomUUID();
        Session expired = new Session("42", Instant.now().minusSeconds(1));
        Session revoked = new Session("42", Instant.now().plusSeconds(60));
        revoked.setRevoked(true);
        when(sessionRepository.findById(expiredId)).thenReturn(Optional.of(expired));
        when(sessionRepository.findById(revokedId)).thenReturn(Optional.of(revoked));
        when(sessionRepository.findById(missingId)).thenReturn(Optional.empty());

        assertThat(sessionService.isSessionValid(expiredId)).isFalse();
        assertThat(sessionService.isSessionValid(revokedId)).isFalse();
        assertThat(sessionService.isSessionValid(missingId)).isFalse();
    }

    @Test
    void revokeSessionMarksExistingSessionRevoked() {
        UUID sessionId = UUID.randomUUID();
        Session session = new Session("42", Instant.now().plusSeconds(60));
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        sessionService.revokeSession(sessionId);

        assertThat(session.isRevoked()).isTrue();
        verify(sessionRepository).save(session);
    }

    @Test
    void revokeSessionDoesNothingWhenSessionMissing() {
        UUID sessionId = UUID.randomUUID();
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        sessionService.revokeSession(sessionId);

        verify(sessionRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void extendSessionUpdatesExpiryForExistingSession() {
        UUID sessionId = UUID.randomUUID();
        Instant newExpiry = Instant.now().plusSeconds(120);
        Session session = new Session("42", Instant.now().plusSeconds(60));
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        sessionService.extendSession(sessionId, newExpiry);

        assertThat(session.getExpiresAt()).isEqualTo(newExpiry);
        verify(sessionRepository).save(session);
    }
}

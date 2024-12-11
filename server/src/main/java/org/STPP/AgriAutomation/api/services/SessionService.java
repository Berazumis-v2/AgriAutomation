package org.STPP.AgriAutomation.api.services;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.STPP.AgriAutomation.api.repositories.SessionRepository;
import org.STPP.AgriAutomation.data.entities.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    public Session createSession(String userId, Instant expiresAt) {
        Session session = new Session(userId, expiresAt);
        return sessionRepository.save(session);
    }

    public Optional<Session> getSession(UUID sessionId) {
        return sessionRepository.findById(sessionId);
    }

    public boolean isSessionValid(UUID sessionId) {
        Optional<Session> sessionOpt = sessionRepository.findById(sessionId);
        if (sessionOpt.isPresent()) {
            Session session = sessionOpt.get();
            return !session.isRevoked() && session.getExpiresAt().isAfter(Instant.now());
        }
        return false;
    }

    public void revokeSession(UUID sessionId) {
        sessionRepository.findById(sessionId).ifPresent(session -> {
            session.setRevoked(true);
            sessionRepository.save(session);
        });
    }

    public void extendSession(UUID sessionId, Instant newExpiry) {
        sessionRepository.findById(sessionId).ifPresent(session -> {
            session.setExpiresAt(newExpiry);
            sessionRepository.save(session);
        });
    }
}

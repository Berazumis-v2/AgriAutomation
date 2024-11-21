package org.STPP.AgriAutomation.api.repositories;

import java.util.Optional;

import org.STPP.AgriAutomation.data.entities.RefreshToken;
import org.STPP.AgriAutomation.data.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    int deleteByUser(User user);
}

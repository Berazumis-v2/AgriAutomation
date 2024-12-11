package org.STPP.AgriAutomation.api.repositories;

import java.util.UUID;

import org.STPP.AgriAutomation.data.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
}

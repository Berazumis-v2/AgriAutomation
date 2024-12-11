package org.STPP.AgriAutomation.api.repositories;

import java.util.Optional;

import org.STPP.AgriAutomation.data.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
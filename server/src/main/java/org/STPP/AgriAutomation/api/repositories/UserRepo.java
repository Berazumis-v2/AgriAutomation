package org.STPP.AgriAutomation.api.repositories;

import java.util.Optional;

import org.STPP.AgriAutomation.data.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}

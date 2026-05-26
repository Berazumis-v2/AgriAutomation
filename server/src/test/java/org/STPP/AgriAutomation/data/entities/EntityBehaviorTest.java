package org.STPP.AgriAutomation.data.entities;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.Test;

class EntityBehaviorTest {

    @Test
    void userAuthoritiesReflectAssignedRoles() {
        User user = new User();
        user.setUsername("alice");
        user.setPassword("password");
        user.setRoles(Set.of(new Role(Role.USER), new Role(Role.ADMIN)));

        assertThat(user.getAuthorities())
                .extracting(authority -> authority.getAuthority())
                .containsExactlyInAnyOrder(Role.USER, Role.ADMIN);
        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.isCredentialsNonExpired()).isTrue();
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void sensorLifecycleCallbacksRefreshReadingTimestamp() throws InterruptedException {
        Sensor sensor = new Sensor();

        sensor.prePersist();
        LocalDateTime firstTimestamp = sensor.getReadingTimestamp();
        Thread.sleep(1);
        sensor.preUpdate();

        assertThat(firstTimestamp).isNotNull();
        assertThat(sensor.getReadingTimestamp()).isAfterOrEqualTo(firstTimestamp);
    }

    @Test
    void sessionConstructorDefaultsRevokedToFalse() {
        Instant expiresAt = Instant.now().plusSeconds(60);

        Session session = new Session("42", expiresAt);

        assertThat(session.getUserId()).isEqualTo("42");
        assertThat(session.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(session.isRevoked()).isFalse();
    }
}

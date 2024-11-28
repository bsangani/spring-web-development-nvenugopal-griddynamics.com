package com.gd.ecom.repository;

import com.gd.ecom.entity.SpringSession;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SpringSessionRepositoryTest {

    @Autowired
    private SessionRepositoryApp sessionRepositoryApp;

    private SpringSession session;

    @BeforeEach
     void setUp() {
        session = SpringSession.builder()
                .primaryId("primaryId123")
                .sessionId("sessionId123")
                .creationTime(System.currentTimeMillis())
                .lastAccessTime(System.currentTimeMillis())
                .maxInactiveInterval(1800)
                .expiryTime(System.currentTimeMillis() + 1800000)
                .principalName("testUser")
                .build();

        sessionRepositoryApp.deleteAll();
    }


    @Test
    @DisplayName("Save Spring Session")
    void testSaveSpringSession() {
        SpringSession savedSession = sessionRepositoryApp.save(session);
        assertThat(savedSession).isNotNull();
        assertThat(savedSession.getPrimaryId()).isEqualTo("primaryId123");
        assertThat(savedSession.getSessionId()).isEqualTo("sessionId123");
        assertThat(savedSession.getPrincipalName()).isEqualTo("testUser");
    }

    @Test
    @DisplayName("Find Spring Session by ID")
    void testFindSpringSessionById() {
        sessionRepositoryApp.save(session);
        Optional<SpringSession> foundSession = sessionRepositoryApp.findById("primaryId123");
        assertThat(foundSession).isPresent();
        assertThat(foundSession.get().getSessionId()).isEqualTo("sessionId123");
    }

    @Test
    @DisplayName("Update Spring Session")
    void testUpdateSpringSession() {
        SpringSession savedSession = sessionRepositoryApp.save(session);
        savedSession.setPrincipalName("updatedUser");
        SpringSession updatedSession = sessionRepositoryApp.save(savedSession);
        assertThat(updatedSession.getPrincipalName()).isEqualTo("updatedUser");
    }

    @Test
    @DisplayName("Delete Spring Session")
    void testDeleteSpringSession() {
        SpringSession savedSession = sessionRepositoryApp.save(session);
        sessionRepositoryApp.delete(savedSession);
        Optional<SpringSession> deletedSession = sessionRepositoryApp.findById("primaryId123");
        assertFalse(deletedSession.isPresent());
    }

    @Test
    @DisplayName("Find All Spring Sessions")
    void testFindAllSpringSessions() {
        SpringSession session1 = SpringSession.builder()
                .primaryId("primaryId123")
                .sessionId("sessionId123")
                .creationTime(System.currentTimeMillis())
                .lastAccessTime(System.currentTimeMillis())
                .maxInactiveInterval(1800)
                .expiryTime(System.currentTimeMillis() + 1800000)
                .principalName("testUser1")
                .build();

        SpringSession session2 = SpringSession.builder()
                .primaryId("primaryId456")
                .sessionId("sessionId456")
                .creationTime(System.currentTimeMillis())
                .lastAccessTime(System.currentTimeMillis())
                .maxInactiveInterval(1800)
                .expiryTime(System.currentTimeMillis() + 1800000)
                .principalName("testUser2")
                .build();

        sessionRepositoryApp.save(session1);
        sessionRepositoryApp.save(session2);

        Iterable<SpringSession> sessions = sessionRepositoryApp.findAll();
        assertThat(sessions).isNotEmpty();
        assertThat(sessions).hasSize(2);
    }
}

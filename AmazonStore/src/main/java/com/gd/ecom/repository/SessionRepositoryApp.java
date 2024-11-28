package com.gd.ecom.repository;

import com.gd.ecom.entity.SpringSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepositoryApp extends JpaRepository<SpringSession,String> {
    Optional<SpringSession> findBySessionId(String id);
}

package com.electricity.backend.repository;

import com.electricity.backend.entity.SessionToken;
import com.electricity.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SessionTokenRepository extends JpaRepository<SessionToken, String> {
    Optional<SessionToken> findByToken(String token);
    void deleteByUser(User user);
    void deleteByExpiryTimeBefore(LocalDateTime now);
}

package com.backend.phi.repository;

import com.backend.phi.entity.PasswordResetToken;
import com.backend.phi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    Optional<PasswordResetToken> findByToken(String token);
    
    Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);
    
    @Modifying
    @Query("DELETE FROM PasswordResetToken p WHERE p.expiresAt < ?1")
    void deleteExpiredTokens(Instant now);
    
    @Modifying
    @Query("UPDATE PasswordResetToken p SET p.used = true WHERE p.user = ?1 AND p.used = false")
    void invalidateUserTokens(User user);
}


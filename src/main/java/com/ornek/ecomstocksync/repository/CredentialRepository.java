package com.ornek.ecomstocksync.repository;

import com.ornek.ecomstocksync.entity.Credential;
import com.ornek.ecomstocksync.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, Long> {
    
    List<Credential> findByPlatform(Platform platform);
    
    List<Credential> findByPlatformAndIsActiveTrue(Platform platform);
    
    Optional<Credential> findByPlatformAndCredentialType(Platform platform, String credentialType);
    
    @Query("SELECT c FROM Credential c WHERE c.platform = :platform AND c.credentialType = :credentialType " +
           "AND c.isActive = true AND (c.expiresAt IS NULL OR c.expiresAt > :now)")
    Optional<Credential> findActiveCredential(Platform platform, String credentialType, LocalDateTime now);
    
    @Query("SELECT c FROM Credential c WHERE c.isActive = true AND c.expiresAt IS NOT NULL AND c.expiresAt < :now")
    List<Credential> findExpiredCredentials(LocalDateTime now);
    
    boolean existsByPlatformAndCredentialType(Platform platform, String credentialType);
}

package com.finbank.auth_service.repository;

import com.finbank.auth_service.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    /*
    For JPQL - We need to provide
    Entity Name and Entity Attributes rather than actual table name and column in database.
    * */
    @Modifying
    @Query(
            """
                    UPDATE RefreshToken r
                    SET r.revoked  = true
                    WHERE r.user.id = :userId
                    AND r.revoked = false
            """
    )
    int revokeAllByUserId(@Param("userId") Long userId);
}
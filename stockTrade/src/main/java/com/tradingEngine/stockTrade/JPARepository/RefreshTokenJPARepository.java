package com.tradingEngine.stockTrade.JPARepository;

import com.tradingEngine.stockTrade.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenJPARepository extends JpaRepository<RefreshToken, Long> {

    Optional<Object>findByToken(String token);

    void deleteByToken(String token);
}

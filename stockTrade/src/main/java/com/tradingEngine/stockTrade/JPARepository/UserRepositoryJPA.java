package com.tradingEngine.stockTrade.JPARepository;

import com.tradingEngine.stockTrade.enums.AuthenticationType;
import com.tradingEngine.stockTrade.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepositoryJPA extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> findByProviderIdAndAuthenticationType(String providerId, AuthenticationType authenticationType);

}

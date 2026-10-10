package com.tradingEngine.stockTrade.JPARepository;

import com.tradingEngine.stockTrade.enums.RoleType;
import com.tradingEngine.stockTrade.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity,Long> {

    Optional<RoleEntity> findByRoleType(RoleType roleType);
}

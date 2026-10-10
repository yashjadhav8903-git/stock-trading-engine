package com.tradingEngine.stockTrade.JPARepository;

import com.tradingEngine.stockTrade.enums.PermissionType;
import com.tradingEngine.stockTrade.model.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity,Long> {

    Optional<PermissionEntity> findByPermissionType(PermissionType permissionType);

}

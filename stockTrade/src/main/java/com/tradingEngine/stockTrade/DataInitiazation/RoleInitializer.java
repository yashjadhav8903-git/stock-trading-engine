package com.tradingEngine.stockTrade.DataInitiazation;

import com.tradingEngine.stockTrade.JPARepository.PermissionRepository;
import com.tradingEngine.stockTrade.JPARepository.RoleRepository;
import com.tradingEngine.stockTrade.JPARepository.UserRepositoryJPA;
import com.tradingEngine.stockTrade.enums.AuthenticationType;
import com.tradingEngine.stockTrade.enums.PermissionType;
import com.tradingEngine.stockTrade.enums.RoleType;
import com.tradingEngine.stockTrade.enums.UserStatus;
import com.tradingEngine.stockTrade.model.PermissionEntity;
import com.tradingEngine.stockTrade.model.RoleEntity;
import com.tradingEngine.stockTrade.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepositoryJPA userRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        Set<PermissionEntity> adminPermission = getPermissions(
                PermissionType.ORDER_CREATE,
                PermissionType.ACCOUNT_LOCK,
                PermissionType.ORDER_MODIFY,
                PermissionType.ORDER_CANCEL,
                PermissionType.PORTFOLIO_READ,
                PermissionType.TRADE_READ,
                PermissionType.STOCK_LIST,
                PermissionType.TRADE_CASH,
                PermissionType.TRADE_VIEW
        );

        Set<PermissionEntity> TraderPermission = getPermissions(
                PermissionType.ORDER_CREATE,
                PermissionType.ORDER_MODIFY,
                PermissionType.ORDER_CANCEL,
                PermissionType.PORTFOLIO_READ,
                PermissionType.TRADE_READ
        );

        Set<PermissionEntity> SystemPermission = getPermissions(
                PermissionType.STOCK_LIST,
                PermissionType.ACCOUNT_LOCK,
                PermissionType.TRADE_CASH,
                PermissionType.TRADE_VIEW

        );

        RoleEntity adminRole = roleRepository.findByRoleType(RoleType.ADMIN)
                .map(role -> {
                    role.setPermission(adminPermission);
                    return roleRepository.save(role);
                })
                .orElseGet(() -> roleRepository.save(
                        RoleEntity.builder()
                                .roleType(RoleType.ADMIN)
                                .permission(adminPermission)
                                .build()
                ));

        RoleEntity system_Manager = roleRepository.findByRoleType(RoleType.SYSTEM_MANAGER)
                .map(role -> {
                    role.setPermission(SystemPermission);
                    return roleRepository.save(role);
                })
                .orElseGet(() -> roleRepository.save(
                   RoleEntity.builder()
                           .roleType(RoleType.SYSTEM_MANAGER)
                           .permission(SystemPermission)
                           .build()
                ));

        roleRepository.findByRoleType(RoleType.TRADER)
                .map(role -> {
                    role.setPermission(TraderPermission);
                    return roleRepository.save(role);
                })
                .orElseGet(() -> roleRepository.save(
                        RoleEntity.builder()
                                .roleType(RoleType.TRADER)
                                .permission(TraderPermission)
                                .build()
                ));


        // admin
        String adminEmail = "admin@stock.com";
        if(!userRepository.existsByUsername(adminEmail)) {

            User user = User.builder()
                    .username(adminEmail)
                    .cashBalance(BigDecimal.ZERO)
                    .userStatus(UserStatus.ACTIVE)
                    .authenticationType(AuthenticationType.EMAIL)
                    .reservedBalance(BigDecimal.ZERO)
                    .password(passwordEncoder.encode("admin0812"))
                    .roles(Set.of(system_Manager))
                    .providerId("DirectInitializer")
                    .build();

            userRepository.save(user);
            log.info("🔥 Default ADMIN user created with permissions! Email: {}", adminEmail);
        } else {
            log.info("✅ ADMIN user already exists in DB. Skipping initialization.");
        }

        // system admin
        String systemManager = "systemManager@stock.com";
        if(!userRepository.existsByUsername(systemManager)) {

            User user = User.builder()
                    .username(systemManager)
                    .cashBalance(BigDecimal.ZERO)
                    .userStatus(UserStatus.ACTIVE)
                    .authenticationType(AuthenticationType.EMAIL)
                    .reservedBalance(BigDecimal.ZERO)
                    .password(passwordEncoder.encode("systemManager3035"))
                    .roles(Set.of(adminRole))
                    .providerId("DirectInitializer")
                    .build();

            userRepository.save(user);
            log.info("🔥 Default SYSTEM MANAGER user created with permissions! Email: {}", systemManager);
        } else {
            log.info("✅ SYSTEM MANAGER user already exists in DB. Skipping initialization.");
        }

    }

    private Set<PermissionEntity> getPermissions(PermissionType... permission) {
        return Stream.of(permission)
                .map(type -> permissionRepository.findByPermissionType(type)
                        .orElseGet(() -> permissionRepository.save(PermissionEntity
                                .builder()
                                .permissionType(type)
                                .build()))).collect(Collectors.toSet());
    }
}

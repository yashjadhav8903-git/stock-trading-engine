package com.tradingEngine.stockTrade.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradingEngine.stockTrade.enums.AuthenticationType;
import com.tradingEngine.stockTrade.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = true)
    private String password;

    @Column(name = "cash_balance", nullable = false)
    private BigDecimal cashBalance;

    @Column(name = "reserved_balance", nullable = false)
    private BigDecimal reservedBalance;

    @Enumerated(EnumType.STRING)
    private AuthenticationType authenticationType;

    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus userStatus;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)  //--> should be retrieved from the database immediately when the parent entity is loaded.
    @Enumerated(EnumType.STRING)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles = new HashSet<>();

    public User(Long id, String username, String password, BigDecimal cashBalance, BigDecimal reservedBalance, AuthenticationType authenticationType, String providerId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.cashBalance = cashBalance;
        this.reservedBalance = reservedBalance;
        this.authenticationType = authenticationType;
        this.providerId = providerId;
    }

    public User() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        // add roles
        for(RoleEntity role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleType().name()));

            // add permissions
            for(PermissionEntity permission : role.getPermission()){
                authorities.add(new SimpleGrantedAuthority(permission.getPermissionType().getPermission()));
            }
        }
        return authorities;
    }
}

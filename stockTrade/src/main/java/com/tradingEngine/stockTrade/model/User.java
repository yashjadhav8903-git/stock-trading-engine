package com.tradingEngine.stockTrade.model;

import com.tradingEngine.stockTrade.enums.AuthenticationType;
import com.tradingEngine.stockTrade.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}

package com.tradingEngine.stockTrade.model;

import java.math.BigDecimal;
import java.util.Objects;

public class User {

    private Long id;
    private String username;
    private String email;
    private String password;
    private BigDecimal cashBalance;
    private BigDecimal reservedBalance;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public BigDecimal getCashBalance() {
        return cashBalance;
    }

    public void setCashBalance(BigDecimal cashBalance) {
        this.cashBalance = cashBalance;
    }

    public BigDecimal getReservedBalance() {
        return reservedBalance;
    }

    public void setReservedBalance(BigDecimal reservedBalance) {
        this.reservedBalance = reservedBalance;
    }

    public User(Long id, String username, String email, String password, BigDecimal cashBalance, BigDecimal reservedBalance) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.cashBalance = cashBalance;
        this.reservedBalance = reservedBalance;
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
}

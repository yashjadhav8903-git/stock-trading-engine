package com.tradingEngine.stockTrade.service;

import com.tradingEngine.stockTrade.enums.UserStatus;
import com.tradingEngine.stockTrade.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void addCash(Long userId, BigDecimal cashAmount){
        if(cashAmount == null || cashAmount.compareTo(BigDecimal.ZERO)<=0){
            throw new IllegalArgumentException("Amount must be positive");
        }

        int rowUpdated = userRepository.addCash(userId, cashAmount);

        if(rowUpdated == 0){
            throw new UsernameNotFoundException("User not found with ID: " + userId);
        }
    }


    @Transactional
    public void UpdateUserStatus(Long userId, String statusInput){
        if(statusInput == null || statusInput.trim().isEmpty()){
            throw new IllegalArgumentException("Status input cannot be null or empty");
        }

        UserStatus userStatus;
        try{
            userStatus=UserStatus.valueOf(statusInput.trim().toUpperCase());
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status provided: " + statusInput
                    + ". Allowed values are: ACTIVE, INACTIVE, LOCKED ❌");
        }

        int rowUpdated = userRepository.statusUpdate(userId, userStatus);

        if(rowUpdated == 0){
            throw new UsernameNotFoundException("User not found with ID: " + userId);
        }
    }

}

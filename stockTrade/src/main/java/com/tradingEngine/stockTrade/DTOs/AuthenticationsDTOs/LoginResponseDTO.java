package com.tradingEngine.stockTrade.DTOs.AuthenticationsDTOs;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private Long userId;
    private String text;
    private String jwtToken;
    private String refreshToken;

}

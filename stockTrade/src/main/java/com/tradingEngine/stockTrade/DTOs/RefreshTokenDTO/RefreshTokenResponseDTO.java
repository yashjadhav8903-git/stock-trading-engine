package com.tradingEngine.stockTrade.DTOs.RefreshTokenDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenResponseDTO {

    private String jwtToken;
    private String refreshToken;

}

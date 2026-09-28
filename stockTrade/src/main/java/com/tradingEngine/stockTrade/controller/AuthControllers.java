package com.tradingEngine.stockTrade.controller;

import com.tradingEngine.stockTrade.DTOs.AuthenticationsDTOs.LoginRequestDTO;
import com.tradingEngine.stockTrade.DTOs.AuthenticationsDTOs.LoginResponseDTO;
import com.tradingEngine.stockTrade.DTOs.AuthenticationsDTOs.SignUpRequestDTO;
import com.tradingEngine.stockTrade.DTOs.AuthenticationsDTOs.SignUpResponseDTO;
import com.tradingEngine.stockTrade.DTOs.RefreshTokenDTO.RefreshTokenRequestDTO;
import com.tradingEngine.stockTrade.DTOs.RefreshTokenDTO.RefreshTokenResponseDTO;
import com.tradingEngine.stockTrade.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthControllers {

    private final AuthService authService;
    public AuthControllers(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<SignUpResponseDTO> register(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        SignUpResponseDTO signUpResponseDTO = authService.signUp(signUpRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(signUpResponseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO login = authService.login(loginRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(login);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logOut(@RequestBody RefreshTokenRequestDTO  refreshTokenRequestDTO) {
        authService.logout(refreshTokenRequestDTO.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).body("Successfully logged out");
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDTO>  refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        RefreshTokenResponseDTO refreshTokenResponseDTO = authService.refreshToken(refreshTokenRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(refreshTokenResponseDTO);
    }
}
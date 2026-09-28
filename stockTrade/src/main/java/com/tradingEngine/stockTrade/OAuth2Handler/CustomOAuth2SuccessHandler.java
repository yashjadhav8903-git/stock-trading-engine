package com.tradingEngine.stockTrade.OAuth2Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingEngine.stockTrade.DTOs.AuthenticationsDTOs.LoginResponseDTO;
import com.tradingEngine.stockTrade.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component

public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final ObjectMapper objectMapper;

    // 🌟 Explicit constructor with @Lazy to break circular dependency cycle
    public CustomOAuth2SuccessHandler(@Lazy AuthService authService,ObjectMapper objectMapper) {
        this.authService = authService;
        this.objectMapper = objectMapper;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // incoming request
        log.info("Incoming Request come to CustomOAuth2SuccessHandler {} " , request.getRequestURI());

        //Google ke token ko Authentication token me convert krna
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

        // us Token se Registration Id Extract krlo
        String registrationId = token.getAuthorizedClientRegistrationId();

        // us authentication se user information extract kro like photo,email,numberNumber etc
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // Call handleOAuth2Registration method
        LoginResponseDTO  loginResponseDTO = authService.handleOAuth2Registration(oAuth2User, registrationId);

        // return that response to user and frontend
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), loginResponseDTO);

    }

}

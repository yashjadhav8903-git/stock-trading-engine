package com.tradingEngine.stockTrade.service;

import com.tradingEngine.stockTrade.DTOs.AuthenticationsDTOs.LoginRequestDTO;
import com.tradingEngine.stockTrade.DTOs.AuthenticationsDTOs.LoginResponseDTO;
import com.tradingEngine.stockTrade.DTOs.AuthenticationsDTOs.SignUpRequestDTO;
import com.tradingEngine.stockTrade.DTOs.AuthenticationsDTOs.SignUpResponseDTO;
import com.tradingEngine.stockTrade.DTOs.RedisDTOs.RefreshTokenRedisDTO;
import com.tradingEngine.stockTrade.DTOs.RefreshTokenDTO.RefreshTokenRequestDTO;
import com.tradingEngine.stockTrade.DTOs.RefreshTokenDTO.RefreshTokenResponseDTO;
import com.tradingEngine.stockTrade.OAuth2Handler.OAuth2HelperMethods;
import com.tradingEngine.stockTrade.SpringBootSecurity.JwtAuthUtils;
import com.tradingEngine.stockTrade.enums.AuthenticationType;
import com.tradingEngine.stockTrade.JPARepository.UserRepositoryJPA;
import com.tradingEngine.stockTrade.enums.UserStatus;
import com.tradingEngine.stockTrade.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
public class AuthService {

    private final UserRepositoryJPA  userRepositoryJPA;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager  authenticationManager;
    private final JwtAuthUtils  jwtAuthUtils;
    private final RefreshTokenInternalService  refreshTokenInternalService;
    private final OAuth2HelperMethods  oAuth2HelperMethods;

    public  AuthService(UserRepositoryJPA userRepositoryJPA, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
                        JwtAuthUtils jwtAuthUtils,RefreshTokenInternalService refreshTokenInternalService,
                        OAuth2HelperMethods oAuth2HelperMethods) {
        this.userRepositoryJPA = userRepositoryJPA;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtAuthUtils = jwtAuthUtils;
        this.refreshTokenInternalService = refreshTokenInternalService;
        this.oAuth2HelperMethods = oAuth2HelperMethods;
    }

    @Transactional
    public User signInInternal(SignUpRequestDTO signUpRequestDTO, AuthenticationType  authenticationType ,String providerId) {
        String ActiveUsername = signUpRequestDTO.getUsername().toLowerCase().trim();

        // check in DB
        if (userRepositoryJPA.existsByUsername(ActiveUsername)) {
            throw new IllegalArgumentException("Duplicate Username ❌" + ActiveUsername);
        }


        User user = User.builder()
                .username(ActiveUsername)
                .authenticationType(authenticationType)
                .providerId(providerId)
                .cashBalance(BigDecimal.ZERO) // initial cashBalance Zero hai kyu Google se login ke time cash add nahi kr sakte isliye 
                .userStatus(UserStatus.ACTIVE)
                .reservedBalance(BigDecimal.ZERO)
                .build();

        if (authenticationType == AuthenticationType.EMAIL && signUpRequestDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(signUpRequestDTO.getPassword()));
        }

        User save = userRepositoryJPA.save(user);
        log.info("New user registered successfully with username: {}", ActiveUsername);
        return save;
    }

    @Transactional
    public SignUpResponseDTO signUp(SignUpRequestDTO signUpRequestDTO) {
        User user = signInInternal(signUpRequestDTO, AuthenticationType.EMAIL, null);
        return new SignUpResponseDTO(user.getId(),user.getUsername());
    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {

        try {
            String activeUser = loginRequestDTO.getUsername().toLowerCase().trim();

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(activeUser, loginRequestDTO.getPassword()));

            User user = (User) authentication.getPrincipal();
            log.info("User logged in successfully: {}", activeUser);
            String text = "Welcome " + user.getUsername();

            String token = jwtAuthUtils.generateJwtToken(user);

            RefreshTokenRedisDTO refreshToken = refreshTokenInternalService.createRefreshToken(user.getUsername());

            return new LoginResponseDTO(user.getId(), text, token, refreshToken.getToken());
        }catch (Exception e) {
            log.error("LOGIN FAILED EXCEPTION: {}", e.getMessage()); // Full Stack Trace Console me print hoga
            throw e;
        }
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenInternalService.deleteRefreshToken(refreshToken);
    }


    @Transactional
    public RefreshTokenResponseDTO refreshToken(RefreshTokenRequestDTO requestDTO) {

        // getFrom redis and DB
        RefreshTokenRedisDTO tokenFromRedisOrDB = refreshTokenInternalService.getTokenFromRedisOrDB(requestDTO.getRefreshToken());

        refreshTokenInternalService.expiryCheckToRefreshToken(tokenFromRedisOrDB);

        RefreshTokenRedisDTO refreshTokenRedisDTO = refreshTokenInternalService.tokenRotation(tokenFromRedisOrDB);

        User user = userRepositoryJPA.findByUsername(tokenFromRedisOrDB.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        String jwtToken = jwtAuthUtils.generateJwtToken(user);

        return new RefreshTokenResponseDTO(
                jwtToken,
                refreshTokenRedisDTO.getToken()
        );
    }


    @Transactional
    public LoginResponseDTO handleOAuth2Registration(OAuth2User oAuth2User, String registrationId) {

        //--> fetch AuthProviderType and ProviderId (Ex.Google,Github) kaha se login kiya hai.
        AuthenticationType authenticationType = oAuth2HelperMethods.getRegistrationIdFromAuthProviderType(registrationId);

        // get providerId
        String providerId = oAuth2HelperMethods.getProviderIdFromOAuth2User(oAuth2User, registrationId);

        // choice email id aur registration id
        String determineUser = oAuth2HelperMethods.getDetermineUsernameFromOAuth2User(oAuth2User,registrationId,providerId);


        // email ko lower latter me rakho
        String email = oAuth2User.getAttribute("email");
        if(email != null && !email.isEmpty()) {
            email = email.toLowerCase().trim();
        }

        // --> check this ProviderId and ProviderType ka user file se Database me hai kya ? ager nahi Create kro
        User userEntity = userRepositoryJPA.findByProviderIdAndAuthenticationType(registrationId,authenticationType).orElse(null);

        // --> Same Email wala banda firse login nahi kr data fir wo Dusre Provider sahi kyu na ho ( Ager google ke ek eamil id se login kiya hai toh github se bhi us email id se login nahi kr sakta 🔥👍)
        User emailUser = (email != null && !email.isBlank()) ?
                userRepositoryJPA.findByUsername(email).orElse(null) : null;

        // ager dono null hai userEntity and emailUser means New User hai signup krlo
        if(userEntity == null && emailUser == null) {
            // 1 -> signUp New User
            SignUpRequestDTO signUpRequestDTO = new SignUpRequestDTO(
                    determineUser,
                    null
            );

            userEntity = signInInternal(signUpRequestDTO,authenticationType,providerId);
            log.info("New User created via signupInternal: {}", userEntity.getUsername());

            // SCENARIO B: Account Linking (Email already black hone ki jagah link hoga)
        } else if(userEntity == null && emailUser != null) {
            // set data to already existing account
            userEntity = emailUser;
            userEntity.setProviderId(providerId);
            userEntity.setAuthenticationType(authenticationType);
            userRepositoryJPA.save(userEntity);
            log.info("Linked provider {} to existing email: {}", authenticationType, email);
        } else {
            // SCENARIO C: Regular OAuth2 Returning User -> // email mila hai or wo email apke username se match nahi krta.
            if(email != null && !email.isBlank() && !email.equals(userEntity.getUsername())) {
                userEntity.setUsername(email);
                userRepositoryJPA.save(userEntity);
            }
        }

        // Token generation
        String text = "Welcome ,Login With " + authenticationType.name();
        String JwtAccessToken = jwtAuthUtils.generateJwtToken(userEntity);
        String refreshToken = refreshTokenInternalService.createRefreshToken(userEntity.getUsername()).getToken();

        return new LoginResponseDTO(userEntity.getId(),text,JwtAccessToken,refreshToken);
    }
}

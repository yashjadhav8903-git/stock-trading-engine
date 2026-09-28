package com.tradingEngine.stockTrade.OAuth2Handler;

import com.tradingEngine.stockTrade.enums.AuthenticationType;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;


@Component
public class OAuth2HelperMethods {


    // Check Provider Identity
    // 1 --> getRegistrationIdFromAuthProviderType
    public AuthenticationType getRegistrationIdFromAuthProviderType(String registrationId){
        return switch (registrationId.toLowerCase()){
          case "google" -> AuthenticationType.GOOGLE;
          case "github" -> AuthenticationType.GITHUB;
            default -> throw new IllegalStateException("Unsupported OAuth2 Provider Type : " + registrationId.toLowerCase());
        };
    }


    // 2 --> getProviderIdFromOAuth2User
    public String getProviderIdFromOAuth2User(OAuth2User oAuth2User, String registrationId){
        String providerId = switch (registrationId.toLowerCase()){
            case "google" -> oAuth2User.getAttribute("sub");
            case "github" -> oAuth2User.getAttribute("id");
            default -> throw new IllegalStateException("Unsupported OAuth2 Provider Id : " + registrationId.toLowerCase());
        };

        if(providerId == null || providerId.isBlank()){
            throw new IllegalStateException("Unable to Determine Provider Id for OAuth2 login : " + registrationId.toLowerCase());
        }
        return providerId;
    }


    // 3 --> getDetermineUsernameFromOAuth2User
    // ager at that time email nahi mila toh providerId add krdo.
    public String getDetermineUsernameFromOAuth2User(OAuth2User oAuth2User, String registrationId,String providerId){
        // if you get email then return that email
        String email = oAuth2User.getAttribute("email");
        if(email != null && !email.isBlank()){
             return email;
        }

        // else add provider id as proof
        return switch (registrationId.toLowerCase()){
            case "google" -> oAuth2User.getAttribute("sub");
            case "github" -> oAuth2User.getAttribute("id");
            default -> providerId;
        };
    }
}

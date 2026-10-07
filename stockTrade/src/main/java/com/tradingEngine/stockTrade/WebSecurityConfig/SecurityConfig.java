package com.tradingEngine.stockTrade.WebSecurityConfig;

import com.tradingEngine.stockTrade.OAuth2Handler.CustomOAuth2SuccessHandler;
import com.tradingEngine.stockTrade.RateLimiter.RateLimiterFilter;
import com.tradingEngine.stockTrade.SpringBootSecurity.JwtAuthFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JwtAuthFilter  jwtAuthFilter;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final RateLimiterFilter rateLimiterFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(
                                "/auth/**",
                                "/oauth2/**",               // 👈 Initiating OAuth2 login (/oauth2/authorization/{provider})
                                "/login/oauth2/code/**",
                                "/doc",
                                "/doc/**",
                                "/doc/index.html",
                                "/index.html",
                                "/static/**",
                                "/",
                                "/ws-trading/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api-docs",
                                "/api-docs/**",
                                "/v3/api-docs/**",
                                "/ws-trading-sockjs/**",
                                "/actuator/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(rateLimiterFilter, JwtAuthFilter.class)


                // Google / GitHub Login Configuration
                .oauth2Login(oAuth2 -> oAuth2
                        .successHandler(customOAuth2SuccessHandler)
                        .failureHandler((request, response, exception) -> {
                            log.error("OAuth2 Error Type: {}, Message: {}",
                                    exception.getClass().getSimpleName(),
                                    exception.getMessage());
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            handlerExceptionResolver.resolveException(request,response,null,exception);
                        })
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .defaultAuthenticationEntryPointFor(
                                // Postman/API Requests: Custom JSON Entry Point
                                (request, response, authException) ->
                                        handlerExceptionResolver.resolveException(request, response, null, authException),
                                // Step 1: Check karo ki kya client ne JSON request mangi hai?
                                new MediaTypeRequestMatcher(MediaType.APPLICATION_JSON)
                        )
                );

        return http.build();
    }

}

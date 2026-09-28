package com.tradingEngine.stockTrade.SpringBootSecurity;

import com.tradingEngine.stockTrade.JPARepository.UserRepositoryJPA;
import com.tradingEngine.stockTrade.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtAuthUtils jwtAuthUtils;
    private final UserRepositoryJPA userRepositoryJPA;


    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    // 1. Un saare endpoints ki list jo public hain
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/auth/**",
            "/doc",
            "/doc/**",
            "/doc/index.html",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api-docs",
            "/api-docs/**",
            "/v3/api-docs/**"
    );

    // 🔥 1️⃣ Refresh endpoint ko skip karo
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        String path = request.getServletPath();
        // Check karo ki incoming path humari excluded list me hai ya nahi

        return EXCLUDED_PATHS.stream()
                .anyMatch(excludePath -> pathMatcher.match(excludePath, path));

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {

            // incoming request
            log.info("Incoming request: {}", request.getRequestURI());

            // header
            String requestTokenHeader = request.getHeader("Authorization");

            // check that header
            if(requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            // clean that token
            String token = requestTokenHeader.substring(7).trim();

            // get username from that token
            String username = jwtAuthUtils.getUsernameFromToken(token);

            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                User user = userRepositoryJPA.findByUsername(username).orElse(null);
                if(user != null) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }

            filterChain.doFilter(request, response);

        } catch (Exception e){
            log.error("Exception in JwtAuthFilter {} ", e.getMessage());
        }
    }
}

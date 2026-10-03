package com.tradingEngine.stockTrade.RateLimiter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RateIntervalUnit;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@Slf4j
public class RateLimiterFilter extends OncePerRequestFilter {

    private final RateLimiterService  rateLimiterService;

    public RateLimiterFilter(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/doc") ||
                path.startsWith("/ws-trading") ||
                path.startsWith("/index.html") ||
                path.equals("/");
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            // incoming request
            String uri = request.getRequestURI();

            // get ip
            String ip = request.getRemoteAddr();

            // Spring Security Context se User Extract karo (JWT Filter ke baad execution hona chahiye)
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userId = (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal()))
                    ? auth.getName()
                    : request.getHeader("userId");

            // key must be userId if userId is null then we use ip and uri combine
            String key = (userId != null && !userId.isBlank()) ? "USER: " + userId : "IP:" + ip;

            // first request is allowed
            boolean allowed = true;

            try {

                if (uri.contains("/buy") || uri.contains("/sell")) {
                   allowed =  rateLimiterService.isRequestAllowed(key + ":ORDERS", 10L, 1L, RateIntervalUnit.SECONDS);
                } else if (uri.contains("/register") || uri.contains("/login")) {
                    allowed = rateLimiterService.isRequestAllowed(key + ":AUTH", 10L, 1L, RateIntervalUnit.MINUTES);
                } else {
                    // 🛡 DEFAULT LIMIT: Baaki pure application ke har API endpoint ke liye
                   allowed = rateLimiterService.isRequestAllowed(key +":GENERAL", 100L, 1L, RateIntervalUnit.MINUTES);
                }

            } catch (Exception e) {
                // Redis is down or network timeout occurred
                log.error("Redis RateLimiter error. Falling back to FAIL-OPEN: {}", e.getMessage());
                // FAIL-OPEN: Set allowed to true so the app keeps working even if Redis is dead
                allowed = true;
            }


            //ager request limit or duration ko exced kr rahi hai toh notAllowed
            if (!allowed) {
                log.warn("❌ Rate limit HIT | IP: {} | URI: {}", ip, uri);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                String jsonResponse = "{\"error\": \"Too Many Requests\", \"message\": \"Please try again later.\"}";
                response.getWriter().write(jsonResponse);

                /// IMP *** --> or yehi sehi return krna hai naki request ko aage jane dena hai
                return;
            } else {
                log.info("✅ Allowed | IP: {} | URI: {} | ALLOWED: {}", ip, uri,allowed);
            }

            // ager bas kuch control me hai toh aage bado --> go to next 👍 ( green flag )
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Unhandled Exception in RateLimiterFilter : {}", request);
        }
    }
}

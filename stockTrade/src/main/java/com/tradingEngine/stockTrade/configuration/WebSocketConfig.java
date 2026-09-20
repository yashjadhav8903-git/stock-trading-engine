package com.tradingEngine.stockTrade.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 1. In-Memory message broker
        // * '/topic' -> public broadcast  e.g. Stocks LTP updates , Market depth
        // * '/queue' -> private message e.g. Specific User Order Executed Alerts
        registry.enableSimpleBroker("/topic","/queue");

        // 2. Client se incoming ke liye prefix (if needed for chat/commands)
        registry.setApplicationDestinationPrefixes("/app");

        // 3. Specific User messaging prefix setup
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Postman Raw WebSocket testing ke liye:
        registry.addEndpoint("/ws-trading")
                .setAllowedOriginPatterns("*");
        // Frontend client is endpoint pe handshaking (connection) karega
        registry.addEndpoint("/ws-trading-sockjs")
                .setAllowedOriginPatterns("*")  // CORS configuration
                .withSockJS();  //fallback support
    }
}

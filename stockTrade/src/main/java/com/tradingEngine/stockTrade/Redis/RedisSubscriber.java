//package com.tradingEngine.stockTrade.Redis;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import org.springframework.data.redis.connection.Message;
//import org.springframework.data.redis.connection.MessageListener;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Service;
//
//import java.nio.charset.StandardCharsets;
//
//@Service
//public class RedisSubscriber implements MessageListener {
//    //Listener
//
//    private final SimpMessagingTemplate simpMessagingTemplate;
//    private final ObjectMapper objectMapper;
//
//    public RedisSubscriber(SimpMessagingTemplate simpMessagingTemplate,ObjectMapper objectMapper) {
//        this.simpMessagingTemplate = simpMessagingTemplate;
//        this.objectMapper = objectMapper;
//    }
//
//
//    @Override
//    public void onMessage(Message message, byte[] pattern) {
//        // Channel name (e.g. "ticker.APPLE")
//        String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
//
//        String body = new String(message.getBody(), StandardCharsets.UTF_8);
//
//        String cleanPrice = body.replace("\"", "").replace("\\", "").trim();
//
//        System.out.println("Redis PubSub Received on channel: " + channel + " -> Price: " + cleanPrice);
//
//        if(channel.startsWith("ticker.")){
//            String symbol = channel.replace("ticker.", "").toUpperCase();
//            simpMessagingTemplate.convertAndSend("/topic/ticker/" + symbol, cleanPrice);
//        } else if (channel.startsWith("orders.")) {
//            String userId = cleanPrice.replace("orders.", "");
//            try {
//                // Raw Redis JSON ko clean JSON string mein format karke WebSocket queue par bhejo
//                Object jsonObject = objectMapper.readValue(body, Object.class);
//                String jsonString = objectMapper.writeValueAsString(jsonObject);
//
//                simpMessagingTemplate.convertAndSend("/queue/orders/" + userId, jsonString);
//            } catch (Exception e) {
//                // Agar fallback String ho
//                simpMessagingTemplate.convertAndSend("/queue/orders/" + userId, body);
//            }
//        }
//    }
//}

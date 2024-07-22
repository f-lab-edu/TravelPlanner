//package com.roamgram.travelDiary.common.websocket;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.ChannelRegistration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.messaging.simp.stomp.StompSessionHandler;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
//import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
//
//@Configuration
//@RequiredArgsConstructor
//@EnableWebSocketMessageBroker
//@Slf4j
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//    private final StompSessionHandler stompHandler;
//    private final HttpJWTHandShakeInterceptor httpHandshakeInterceptor;
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.enableSimpleBroker("/sub");
//        registry.setApplicationDestinationPrefixes("/pub");
//    }
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/stomp/chat")
//                .setAllowedOriginPatterns("*")
//                .addInterceptors(httpHandshakeInterceptor)
//                .withSockJS();
//    }
//
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(stompHandler);
//    }
//
//    @Override
//    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
//        registry.setMessageSizeLimit(512 * 1024); // 메시지 최대 크기를 512KB로 설정
//        registry.setSendTimeLimit(10 * 1000); // 메시지 전송 시간 제한을 10초로 설정
//        registry.setSendBufferSizeLimit(512 * 1024); // 전송 버퍼 사이즈를 512KB로 설정
//    }
//}
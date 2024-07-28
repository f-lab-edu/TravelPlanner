//package com.roamgram.travelDiary.common.websocket.handler;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.stomp.StompCommand;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.messaging.support.ChannelInterceptor;
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;
//
//@Order(Ordered.HIGHEST_PRECEDENCE + 99) // 우선 순위를 높게 설정해서 SecurityFilter들 보다 앞서 실행되게 해준다.
//@Component
//@Slf4j
//public class StompHandler implements ChannelInterceptor { // WebSocket을 이용한 채팅 기능에서 메시지를 가공하고 처리하는 역할
//
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) { // 메시지의 유효성 검사나 변형 처리
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//        StompCommand stompCommand = accessor.getCommand();
//
//        if (stompCommand != null) {
//            handleStompCommand(stompCommand, accessor);
//        }
//        return ChannelInterceptor.super.preSend(message, channel);
//    }
//
//    private void handleStompCommand(StompCommand stompCommand, StompHeaderAccessor accessor) {
//        switch (stompCommand) {
//            case CONNECT:
//                log.debug("CONNECT");
//                break;
//            case ERROR:
//                log.debug("WebSocket Error 처리 코드!!");
//                break;
//        }
//    }
//
//    private void handleSubscribe(StompHeaderAccessor accessor) {
//        if (accessor.getDestination().startsWith(TOPIC_NOTIFICATION)) {
//            log.debug("알림 SUBSCRIBE");
//            return;
//        }
//
//        handleChatRoomSubscription(accessor);
//    }
//
//    private void handleChatRoomSubscription(StompHeaderAccessor accessor) {
//        log.debug("채팅방 SUBSCRIBE");
//
//        UUID chatRoomId = getChatRoomId(accessor);
//        UUID memberId = getMemberId(accessor);
//
//        // 구독하려는 채팅방 참여자가 맞는지 검증
//        validateChatRoomParticipant(chatRoomId, memberId);
//
//        updateSubscription(accessor, chatRoomId, memberId);
//
//        // 채팅방에 입장했는데 상대방이 채팅방에 입장한 상태라면, 메시지 읽음 처리 갱신 요청
//        chatService.getOtherMemberIdByChatRoomId(chatRoomId, memberId)
//                .ifPresent(otherMemberId -> notifyReadCountUpdate(chatRoomId, otherMemberId));
//
//        // 입장한 채팅방과 관련된 채팅 알림 메시지 삭제
//        notificationService.deleteAllNotificationsInChatRoomByMember(memberId, chatRoomId);
//    }
//
//    private void handleUnsubscribe(StompHeaderAccessor accessor) {
//        String destination = accessor.getDestination();
//        if (destination != null && accessor.getDestination().startsWith(TOPIC_NOTIFICATION)) {
//            log.debug("알림 UNSUBSCRIBE");
//            return;
//        }
//
//        // 채팅방 구독 해지 처리
//        handleChatRoomUnsubscription(accessor);
//    }
//
//    private void handleDisconnect(StompHeaderAccessor accessor) {
//        log.debug("웹소켓 DISCONNECT");
//        handleChatRoomUnsubscription(accessor);
//    }
//
//    private void handleChatRoomUnsubscription(StompHeaderAccessor accessor) {
//        log.debug("채팅방 UNSUBSCRIBE");
//
//        Long memberId = getMemberId(accessor);
//        deleteExistingSubscription(accessor);
//        chatService.deleteChatRoomParticipantFromRedis(memberId);
//    }
//}
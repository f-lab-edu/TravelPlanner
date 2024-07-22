//package com.roamgram.travelDiary.common.websocket;
//
//import com.roamgram.travelDiary.common.auth.domain.AuthUser;
//import com.roamgram.travelDiary.common.auth.service.AuthUserService;
//import com.roamgram.travelDiary.common.auth.v2.jwt.JwtProvider;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.http.server.ServletServerHttpRequest;
//import org.springframework.http.server.ServletServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.server.HandshakeInterceptor;
//
//import java.io.IOException;
//import java.util.Map;
//import java.util.UUID;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class HttpJWTHandShakeInterceptor implements HandshakeInterceptor {
//
//    private final JwtProvider jwtProvider;
//
//    private static final String ERROR_MESSAGE = "websocket connection error!!";
//    private final AuthUserService authUserService;
//
//    @Override
//    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
//                                   Map<String, Object> attributes) throws Exception {
//
//        HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
//        HttpServletResponse resp = ((ServletServerHttpResponse) response).getServletResponse();
//
//        return verifyTokenAndStoreMemberId(attributes, resp);
//    }
//
//    private boolean verifyTokenAndStoreMemberId(Map<String, Object> attributes, HttpServletResponse resp)
//            throws IOException {
//        try {
//            AuthUser authUser = authUserService.getCurrentAuthenticatedUser();
//            UUID memberId = authUser.getId();
//
//            attributes.put(ChatUtil.MEMBER_ID, memberId);
//
//            return true;
//        } catch (Exception e) {
//            log.error(ERROR_MESSAGE);
//            CustomResponseUtil.handleTokenVerificationFailure(resp);
//            return false;
//        }
//    }
//
//    @Override
//    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
//                               Exception exception) {
//    }
//}
//

package api.chatapp.config;

import api.chatapp.payload.ChatMessage;
import api.chatapp.payload.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {
    //    note @Slf4j is for logging
    private final SimpMessageSendingOperations messagingTemplate;
    private ConcurrentHashMap<String, Boolean> onlineUsers = new ConcurrentHashMap<>();
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        // You can log the connection event or handle any necessary actions
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null) {
            onlineUsers.put(username, true);
            notifyUserStatus(username, true);
        }
    }
    @EventListener
    public void handleWebSocketDisconnectListener(
            SessionDisconnectEvent event
    ) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username!=null) {
            log.info("User Disconnected: {}", username);
            var chatMessage = ChatMessage
                    .builder()
                    .type(MessageType.LEAVER)
                    .sender(username)
                    .build();
            messagingTemplate.convertAndSend("topic/public",chatMessage);
            onlineUsers.put(username, false);
            notifyUserStatus(username, false);
        }
    }

    private void notifyUserStatus(String username, boolean isOnline) {
        ChatMessage chatMessage = ChatMessage
                .builder()
                .sender(username)
                .type(isOnline ? MessageType.JOIN : MessageType.LEAVER)
                .timestamp(LocalDateTime.now())
                .build();

        // Notify all users about the user's status
        onlineUsers.keySet().forEach(user -> {
            messagingTemplate.convertAndSendToUser(user, "/queue/notifications", chatMessage);
        });
    }
}
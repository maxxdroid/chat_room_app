package api.chatapp.controller;

import api.chatapp.payload.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/{chatroom}")
    @SendTo("user/chat/{chatroom}")
    public ChatMessage privateChat(
            @PathVariable String roomID,
            @Payload ChatMessage chatMessage
    ) {
        return chatMessage;
    }
}

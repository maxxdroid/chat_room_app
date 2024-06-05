package api.chatapp.payload;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    private String content;
    private String sender;
    private String recipient;
    private LocalDateTime timestamp;
    private MessageType type;
}

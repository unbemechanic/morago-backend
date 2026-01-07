package morago.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import morago.jwt.JWTService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            StompCommand command = accessor.getCommand();

            if (StompCommand.CONNECT.equals(command)) {
                Principal user = accessor.getUser();
                if (user == null) {
                    throw new IllegalStateException("Unauthenticated WebSocket connection");
                }
                log.info("Connected to {}", user.getName());
            }

            if (StompCommand.SUBSCRIBE.equals(command)) {
                String destination = accessor.getDestination();
                Principal user = accessor.getUser();

                log.info("SUBSCRIBE - for debug if this uses name or phone number user: {}, sessionId: {}, destination: {}",
                        user != null ? user.getName() : "anonymous",
                        accessor.getSessionId(),
                        destination);

                if (destination != null && destination.startsWith("/user/queue/calls") && user == null) {
                    throw new IllegalArgumentException("Not authorized to subscribe to call queue");
                }
            }

            if (StompCommand.SEND.equals(command)) {
                String destination = accessor.getDestination();
                Principal user = accessor.getUser();

                if (destination != null &&
                        (destination.equals("/app/call.accept") ||
                                destination.equals("/app/call.reject") ||
                                destination.equals("/app/call.end"))) {

                    if (user == null) {
                        throw new IllegalArgumentException("Not authorized to send call action");
                    }

                    Object payload = message.getPayload();
                    if (payload instanceof byte[]) {
                        log.info("SEND - user: {}, destination: {}, payload: {}",
                                user.getName(), destination, new String((byte[]) payload));
                    }
                }
            }
        }

        return message;
    }
}

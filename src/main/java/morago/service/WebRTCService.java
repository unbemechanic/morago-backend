package morago.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import morago.dto.webrtc.WebRTCSignalingMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebRTCService {
    private final SimpMessagingTemplate simpMessagingTemplate;

    private final Map<String, Map<String, Object>> activePeerConnections = new ConcurrentHashMap<>();

    public void initializePeerConnections(String callId, String userId) {
        log.info("Initializing peer connections for call {}, userId {}", callId, userId);

        String connectionId = callId + "-" + userId;
        Map<String, Object> connectionInfo = new ConcurrentHashMap<>();
        connectionInfo.put("callId", callId);
        connectionInfo.put("userId", userId);
        connectionInfo.put("status", "initialized");
        connectionInfo.put("createdAt", LocalDateTime.now());

        activePeerConnections.put(connectionId, connectionInfo);

        WebRTCSignalingMessage message = WebRTCSignalingMessage.builder()
                .callId(Long.valueOf(callId))
                .fromUserId(userId)
                .type("PEER_STARTED")
                .build();

        simpMessagingTemplate.convertAndSend(
                "/topic/webrtc-room/" + callId, message
        );
    }

    public void closePeerConnection(String callId, String userId) {
        log.info("Closing peer connection for call {}, userId {}", callId, userId);

        String connectionId = callId + "-" + userId;
        activePeerConnections.remove(connectionId);

        WebRTCSignalingMessage message = WebRTCSignalingMessage.builder()
                .callId(Long.valueOf(callId))
                .fromUserId(userId)
                .type("PEER_CLOSED")
                .build();

        simpMessagingTemplate.convertAndSend("/topic/webrtc-room/" + callId, message);
    }
}

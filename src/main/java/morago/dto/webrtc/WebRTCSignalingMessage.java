package morago.dto.webrtc;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebRTCSignalingMessage {
    private String type;
    private Long callId;
    private String fromUserId;
    private String toUserId;
    private Object sdp;
    private Object candidate;
}

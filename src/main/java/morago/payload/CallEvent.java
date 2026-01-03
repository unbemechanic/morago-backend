package morago.payload;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CallEvent {
    private String type;
    private Long callId;
    private Long fromUserId;
}

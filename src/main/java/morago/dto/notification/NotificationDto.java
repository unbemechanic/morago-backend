package morago.dto.notification;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import morago.model.Notification;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class NotificationDto {

    private Long id;
    private String type;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
    public static NotificationDto from(Notification n) {
        return NotificationDto.builder()
                .id(n.getId())
                .type(n.getType().name())
                .message(n.getMessage())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}

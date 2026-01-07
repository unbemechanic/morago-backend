package morago.controller;


import lombok.RequiredArgsConstructor;
import morago.dto.notification.NotificationDto;
import morago.security.CustomUserDetails;
import morago.service.NotificationService;
import morago.service.UserNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('CLIENT', 'INTERPRETER')")
@RequestMapping("/user/notifications")
public class UserNotificationController {

    private final UserNotificationService userNotificationService;
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getUserNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {
        List<NotificationDto> notifications = userNotificationService.getUserNotificationsById(userDetails.getId());
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<NotificationDto> notifications = userNotificationService.getUserUnreadNotificationsById(userDetails.getId());
        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id){
        notificationService.markAsRead(id);
    }

}

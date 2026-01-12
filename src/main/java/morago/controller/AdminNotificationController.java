package morago.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import morago.dto.notification.NotificationDto;
import morago.security.CustomUserDetails;
import morago.service.AdminService;
import morago.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/notifications")
@RequiredArgsConstructor
public class AdminNotificationController {

    private final AdminService adminService;
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getAdminNotifications(){
        List<NotificationDto> notifications = adminService.getAdminNotifications();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications(Authentication auth){
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = userDetails.getId();
        log.info("principal id is {}", userId);
        List<NotificationDto> unreadNotifications = adminService.getAdminUnreadNotifications(userId);
        return ResponseEntity.ok(unreadNotifications);
    }

    @PatchMapping("/{id}/read")
    public void markRead(@PathVariable Long id){
        notificationService.markAsRead(id);
    }
}

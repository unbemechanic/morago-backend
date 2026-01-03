package morago.controller;

import lombok.RequiredArgsConstructor;
import morago.dto.notification.NotificationDto;
import morago.service.AdminService;
import morago.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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

    @PatchMapping("/{id}/read")
    public void markRead(@PathVariable Long id){
        notificationService.markAsRead(id);
    }
}

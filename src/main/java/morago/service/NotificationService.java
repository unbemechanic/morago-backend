package morago.service;

import lombok.RequiredArgsConstructor;
import morago.customExceptions.UserNotFoundException;
import morago.dto.notification.NotificationDto;
import morago.enums.NotificationType;
import morago.enums.RoleEnum;
import morago.model.Notification;
import morago.model.User;
import morago.repository.NotificationRepository;
import morago.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserRepository userRepository;


    public void notifyUser(Long userId, NotificationType type, String message) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .message(message)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        simpMessagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                NotificationDto.from(notification));
    }

    public void markAsRead( Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(UserNotFoundException::new);
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
    // ADMIN NOTIFICATION HELPER METHODS
    public void notifyAdminUserCreated(User user) {
        Long adminId = findAdminId();

        notifyUser(
                adminId,
                NotificationType.ADMIN_NEW_USER,
                "New user registered: " + user.getPhoneNumber()
        );
    }

    public void notifyAdminUserProfileFinish(User user) {
        Long adminId = findAdminId();

        notifyUser(
                adminId,
                NotificationType.PROFILE_COMPLETED,
                "User finishied thier profile: " + user.getPhoneNumber()
        );
    }

    public Long findAdminId() {
        return userRepository
                .findFirstByRoles_Name(RoleEnum.ADMIN.name())
                .orElseThrow(() -> new RuntimeException("Admin not found"))
                .getId();

    }
}

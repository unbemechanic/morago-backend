package morago.service;

import lombok.RequiredArgsConstructor;
import morago.customExceptions.UserNotFoundException;
import morago.dto.notification.NotificationDto;
import morago.enums.NotificationType;
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
}

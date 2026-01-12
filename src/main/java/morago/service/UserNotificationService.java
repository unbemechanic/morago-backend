package morago.service;

import lombok.RequiredArgsConstructor;
import morago.dto.notification.NotificationDto;
import morago.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserNotificationService {

    private final NotificationRepository notificationRepository;

    public List<NotificationDto> getUserNotificationsById(Long userId){
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationDto::from).toList();
    }

    public List<NotificationDto> getUserUnreadNotificationsById(Long userId){
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationDto::from)
                .toList();

    }

}

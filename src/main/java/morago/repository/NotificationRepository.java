package morago.repository;

import morago.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Notification> findByUser_IdAndUser_Roles_NameAndIsReadFalseOrderByCreatedAtDesc(
            Long userId,
            String roleName
    );


    long countByUserIdAndIsReadFalse(Long userId);

    List<Notification> findByUser_Roles_Name(String roleName);

    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
}

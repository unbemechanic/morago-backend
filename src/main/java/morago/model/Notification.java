package morago.model;

import jakarta.persistence.*;
import lombok.*;
import morago.enums.NotificationType;
import morago.monitor.Audit;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications")
public class Notification extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationType type;

    private String message;

    @Column(name = "is_read", nullable = false, length = 100)
    private Boolean isRead;


}

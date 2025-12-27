package morago.model.password;

import jakarta.persistence.*;
import lombok.*;
import morago.model.User;
import morago.monitor.Audit;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "password_reset")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordReset extends Audit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name ="token", unique = true, nullable = true, length = 64)
    private String token;

    @Column(name ="reset_code", nullable = true)
    private Integer resetCode;

    @Column(name = "expires_at", nullable = false)
    private java.time.LocalDateTime expiresAt;

    @Column(name = "used", nullable = false)
    private Boolean used = false;

    @Column(name = "code_verified", nullable = false)
    @Builder.Default
    private Boolean codeVerified = false;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
}

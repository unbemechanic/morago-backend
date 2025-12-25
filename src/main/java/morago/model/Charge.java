package morago.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import morago.enums.Status;
import morago.model.client.ClientProfile;
import morago.monitor.Audit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "charges")
public class Charge extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_profile_id")
    private ClientProfile clientProfile;

    private BigDecimal amount;

    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
}

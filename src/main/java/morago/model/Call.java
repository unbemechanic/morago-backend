package morago.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import morago.enums.Status;
import morago.model.client.ClientProfile;
import morago.model.interpreter.InterpreterProfile;
import morago.monitor.Audit;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "calls")
public class Call extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_profile_id")
    private ClientProfile clientProfile;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "interpreter_profile_id")
    private InterpreterProfile interpreterProfile;

    @Column(name = "call_started_at")
    private LocalDateTime callStartedAt;

    @Column(name = "call_ended_at")
    private LocalDateTime callEndedAt;
    private BigInteger duration;

    @Column(name = "total_price")
    private BigDecimal totalPrice;
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "call_topic")
    private CallTopic callTopic;
}

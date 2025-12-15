package morago.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import morago.enums.Status;
import morago.model.client.ClientProfile;
import morago.model.interpreter.InterpreterProfile;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "calls")
public class Call {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clientProfileId")
    private ClientProfile clientProfile;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "interpreterProfileId")
    private InterpreterProfile interpreterProfile;

    private LocalDateTime callStartedAt;
    private LocalDateTime callEndedAt;
    private BigInteger duration;
    private BigDecimal totalPrice;
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "callTopic")
    private CallTopic callTopic;
}

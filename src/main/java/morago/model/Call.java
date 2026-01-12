package morago.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import morago.customExceptions.call.InvalidCallStateException;
import morago.customExceptions.call.SecurityCallException;
import morago.enums.CallState;
import morago.model.client.ClientProfile;
import morago.model.interpreter.InterpreterProfile;
import morago.monitor.Audit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;

@Slf4j
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
    private Instant callStartedAt;

    @Column(name = "call_ended_at")
    private Instant callEndedAt;
    private Long duration;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CallState state;

    @ManyToOne
    @JoinColumn(name = "call_topic")
    private CallTopic callTopic;

    public Call(ClientProfile client, InterpreterProfile interpreter, CallTopic callTopic) {
        this.clientProfile = client;
        this.interpreterProfile = interpreter;
        this.callTopic = callTopic;
        this.state = CallState.CREATED;
    }

    public void transition(CallState from, CallState to) {
        if (state != from) {
            throw new InvalidCallStateException(from, to);
        }
        state = to;
    }
    public boolean canStart() {
        return state == CallState.ACCEPTED;
    }

    public void start(){
        transition(CallState.ACCEPTED, CallState.STARTED);
        callStartedAt = Instant.now();
    }

    public void end(BigDecimal ratePerSecond){
        if (callStartedAt == null) {
            throw new IllegalStateException("Call was never started");
        }

        if (state == CallState.ENDED) {
            return;
        }

        transition(CallState.STARTED, CallState.ENDED);
        callEndedAt = Instant.now();

        duration = Duration.between(callStartedAt, callEndedAt).getSeconds();
        if (duration <= 0) {
            throw new IllegalStateException("Invalid call duration");
        }

        totalPrice = ratePerSecond
                .multiply(BigDecimal.valueOf(duration))
                .setScale(0, RoundingMode.HALF_UP);
        log.info("Call ended in {} seconds", duration);
    }
    public void accept(Long actorId){
        if (!interpreterProfile.getId().equals(actorId)) {
            throw new SecurityCallException("Only interpreters can accept calls");
        }
        transition(CallState.CREATED, CallState.ACCEPTED);
    }

    public void reject(Long actorId){
        if (!interpreterProfile.getId().equals(actorId)) {
            throw new SecurityCallException("Only interpreters can reject calls");
        }
        transition(CallState.CREATED, CallState.REJECTED);
    }

    public void cancel(Long clientId){
        boolean allowed =
                clientProfile.getId().equals(clientId)
                        || interpreterProfile.getId().equals(clientId);

        if (!allowed) {
            throw new SecurityCallException("Not allowed to cancel call");
        }

        if (state == CallState.ENDED || state == CallState.REJECTED) {
            throw new InvalidCallStateException(state, CallState.CANCELLED);
        }

        state = CallState.CANCELLED;
    }
}

package morago.model.interpreter;

import jakarta.persistence.*;
import lombok.*;
import morago.enums.TopikLevel;
import morago.model.Call;
import morago.model.CallTopic;
import morago.model.User;
import morago.monitor.Audit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "interpreter_profile")
public class InterpreterProfile extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "interpreter_languages",
            joinColumns = @JoinColumn(name = "interpreter_id"),
            inverseJoinColumns = @JoinColumn(name = "language_id")
    )
    private Set<Language> languages;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "interpreter_call_topics",
            joinColumns = @JoinColumn(name = "interpreter_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "call_topic_id")
    )
    private Set<CallTopic> callTopics;

    @Enumerated(EnumType.STRING)
    private TopikLevel level;

    @Column(name = "hourly_rate")
    private BigDecimal hourlyRate;

    @Transient
    public BigDecimal getHourlyRatePerSecond() {
        if (hourlyRate == null) {
            return BigDecimal.ZERO;
        }

        return hourlyRate.divide(
                BigDecimal.valueOf(3600),
                6,
                RoundingMode.HALF_UP
        );
    }

    @Column(name = "is_active")
    private Boolean isActive = false;

    @OneToMany(mappedBy = "interpreterProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Call> calls = new ArrayList<>();
}

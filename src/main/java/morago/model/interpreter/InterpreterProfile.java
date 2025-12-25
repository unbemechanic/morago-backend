package morago.model.interpreter;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import morago.model.User;
import morago.monitor.Audit;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "interpreter_profile")
public class InterpreterProfile extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "interpreter_languages",
            joinColumns = @JoinColumn(name = "interpreter_id"),
            inverseJoinColumns = @JoinColumn(name = "language_id")
    )
    private Set<Language> languages;

    private String level;

    @Column(name = "hourly_rate")
    private BigDecimal hourlyRate;

    @Column(name = "is_active")
    private Boolean isActive = false;
}

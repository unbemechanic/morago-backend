package morago.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "interpreters")
public class InterpreterProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "interpreterLanguages",
            joinColumns = @JoinColumn(name = "interpreterId"),
            inverseJoinColumns = @JoinColumn(name = "languageId")
    )
    private Set<Language> languages;

    private String level;
    private BigDecimal hourlyRate;
    private Boolean isActive;
}

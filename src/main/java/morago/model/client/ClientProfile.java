package morago.model.client;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import morago.model.Call;
import morago.model.User;
import morago.monitor.Audit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "client_profile")
public class ClientProfile extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    private BigDecimal balance = BigDecimal.ZERO;
    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "clientProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Call> calls = new ArrayList<>();

    @OneToMany(mappedBy = "clientProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Deposit> deposits = new ArrayList<>();

}

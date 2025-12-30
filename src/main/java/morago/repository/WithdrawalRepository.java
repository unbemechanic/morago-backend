package morago.repository;


import morago.model.interpreter.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {
    List<Withdrawal> findAllByInterpreterProfileId(Long id);
}

package morago.repository;

import morago.model.interpreter.InterpreterWithdrawalRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterpreterWithdrawalRequestRepository extends JpaRepository<InterpreterWithdrawalRequest, Long> {
    List<InterpreterWithdrawalRequest> findAllByInterpreterProfileId(Long id);
}

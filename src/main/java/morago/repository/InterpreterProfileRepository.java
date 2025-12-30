package morago.repository;

import morago.model.interpreter.InterpreterProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterpreterProfileRepository extends JpaRepository<InterpreterProfile, Long> {
}

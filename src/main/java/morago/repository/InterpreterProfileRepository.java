package morago.repository;

import morago.model.interpreter.InterpreterProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterpreterProfileRepository extends JpaRepository<InterpreterProfile, Long> {
    Optional<InterpreterProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}

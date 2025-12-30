package morago.repository.call;

import morago.model.Call;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CallRepository extends JpaRepository<Call, Long> {
    List<Call> findAllByInterpreterProfileId(Long interpreterProfileId);
}

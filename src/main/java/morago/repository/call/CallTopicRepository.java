package morago.repository.call;

import morago.model.CallTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CallTopicRepository extends JpaRepository<CallTopic, Long> {
    Optional<CallTopic> findByTopicId(Long id);
    Optional<CallTopic> findByTopicName(String topicName);

    boolean existsByTopicName(String name);
}

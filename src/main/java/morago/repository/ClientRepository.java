package morago.repository;

import morago.model.client.ClientProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<ClientProfile, Long> {
    ClientProfile findByUserId(Long userId);
}

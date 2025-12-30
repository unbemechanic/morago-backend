package morago.repository;

import morago.dto.admin.DepositResponseDto;
import morago.model.client.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepositRepository extends JpaRepository<Deposit, Long> {
    List<DepositResponseDto> findAllByClientProfileId(Long clientProfileId);
}

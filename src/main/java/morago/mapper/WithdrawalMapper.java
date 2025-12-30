package morago.mapper;

import morago.dto.admin.interpreter.WithdrawalDto;
import morago.model.interpreter.Withdrawal;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WithdrawalMapper {
    WithdrawalDto toDto(Withdrawal withdrawal);
    List<WithdrawalDto> toDtoList(List<Withdrawal> withdrawals);
}

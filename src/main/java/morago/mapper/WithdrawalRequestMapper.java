package morago.mapper;

import morago.dto.admin.interpreter.InterpreterWithdrawalRequestDto;
import morago.model.interpreter.InterpreterWithdrawalRequest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WithdrawalRequestMapper {
    InterpreterWithdrawalRequestDto toDto(InterpreterWithdrawalRequest wr);
    List<InterpreterWithdrawalRequestDto> toDtoList(List<InterpreterWithdrawalRequest> wr);
}

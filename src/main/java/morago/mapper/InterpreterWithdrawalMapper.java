package morago.mapper;

import morago.dto.admin.interpreter.InterpreterWithdrawalRequestDto;
import morago.model.interpreter.InterpreterWithdrawalRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InterpreterWithdrawalMapper {
    InterpreterWithdrawalRequestDto toDto(
            InterpreterWithdrawalRequest entity
    );
}

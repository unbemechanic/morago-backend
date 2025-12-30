package morago.mapper;

import morago.dto.admin.CallResponseDto;
import morago.model.Call;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CallMapper {
    CallResponseDto toDto(Call call);
    List<CallResponseDto> toDtoList(List<Call> calls);
}

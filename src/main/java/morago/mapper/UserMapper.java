package morago.mapper;

import morago.dto.admin.*;
import morago.enums.Status;
import morago.model.Call;
import morago.model.User;
import morago.model.client.Deposit;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", imports = Status.class)
public interface UserMapper {
    // Main
    UserResponseDto toUserResponseDto(User user);

    SingleUserResponseDto toSingleUserResponseDto(User user);

    // Nested
    DepositRequestResponseDto toDepositRequestResponseDto(Deposit deposit);

    DepositResponseDto toDepositResponseDto(Deposit deposit);

    CallResponseDto toCallResponseDto(Call call);

    // Aggregation
    @AfterMapping
    default void enrich(User user, @MappingTarget UserResponseDto.UserResponseDtoBuilder dto) {
        List<DepositRequestResponseDto> depositRequests = new ArrayList<>();
        List<DepositResponseDto> deposits = new ArrayList<>();
        List<CallResponseDto> calls = new ArrayList<>();

        // Client Profile
        if(user.getClientProfile() != null){
            var client = user.getClientProfile();

            //Deposits
            if(client.getDeposits() != null){
                for(Deposit deposit : client.getDeposits()){
                    if(deposit.getStatus() == Status.PENDING){
                      depositRequests.add(toDepositRequestResponseDto(deposit));
                    }else if (deposit.getStatus() == Status.SUCCESS){
                        deposits.add(toDepositResponseDto(deposit));
                    }
                }
            }

            // Client Calls
            if(client.getCalls() != null){
                for(Call call : client.getCalls()){
                    calls.add(toCallResponseDto(call));
                }
            }
        }

        // Interpreter Profile
        if(user.getInterpreterProfile() != null && user.getInterpreterProfile().getCalls() != null){
            for(Call call : user.getInterpreterProfile().getCalls()){
                calls.add(toCallResponseDto(call));
            }
        }
        dto.depositRequests(depositRequests);
        dto.deposits(deposits);
        dto.calls(calls);
    }


}

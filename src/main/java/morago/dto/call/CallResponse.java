package morago.dto.call;

import morago.enums.CallState;
import morago.model.Call;

import java.math.BigDecimal;
import java.time.Instant;

public record CallResponse(
        Long id,
        Long callerId,
        Long calleeId,
        CallState state,
        Instant startedAt,
        Instant endedAt,
        Long duration,
        BigDecimal totalPrice
) {

    public static CallResponse from(Call call) {
        return new CallResponse(
                call.getId(),
                call.getClientProfile().getId(),
                call.getInterpreterProfile().getId(),
                call.getState(),
                call.getCallStartedAt(),
                call.getCallEndedAt(),
                call.getDuration(),
                call.getTotalPrice()
        );
    }
}

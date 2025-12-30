package morago.dto.call;

public record CallCreateRequest(
        Long callerId,
        Long calleeId,
        Long topicId
) {}

package morago.dto.call;

public record CallCreateRequest(
        Long calleeId,
        Long topicId
) {}

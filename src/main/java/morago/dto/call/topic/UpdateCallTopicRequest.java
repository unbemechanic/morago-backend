package morago.dto.call.topic;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCallTopicRequest {
    private String name;
    private Boolean isActive;
}

package morago.dto.call.topic;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CallTopicDto {
    @NotBlank(message = "Topic name is required")
    private String name;

    @Column(name = "is_active")
    private Boolean isActive = false;
}

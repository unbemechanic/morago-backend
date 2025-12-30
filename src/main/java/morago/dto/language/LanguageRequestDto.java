package morago.dto.language;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LanguageRequestDto {
    @NotBlank String name;
}

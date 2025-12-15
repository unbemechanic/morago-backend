package morago.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String issuer;
    private String accessSecret;
    private String refreshSecret;
    private Long accessTokenExpireTime;
    private Long refreshTokenExpireTime;
}

package morago.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import morago.enums.TokenEnum;
import morago.jwt.JWTService;
import morago.model.User;
import morago.repository.UserRepository;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JWTService jwtService;
    private final UserRepository userRepository;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        if(request instanceof ServletServerHttpRequest servletRequest){

            String token = servletRequest.getServletRequest().getParameter("token");

            if(token == null || token.isBlank()){
                log.error("WebSocket handshake failed: missing token");
                throw new IllegalArgumentException("Missing token");
            }

            String username = jwtService.extractUsername(token, TokenEnum.ACCESS);

            User user = userRepository.getByPhoneNumber(username).orElseThrow();

            attributes.put("user", user);
            attributes.put("userId", user.getId());
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {


    }
}

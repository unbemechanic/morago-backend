package morago.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import morago.customExceptions.token.ExpiredJwtTokenException;
import morago.enums.TokenEnum;
import morago.service.CustomUserDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final CustomUserDetailService customUserDetailService;

    public JwtFilter(JWTService jwtService,  CustomUserDetailService customUserDetailService) {
        this.jwtService = jwtService;
        this.customUserDetailService = customUserDetailService;
    }

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.equals("/auth/login") ||
                path.equals("/auth/register") ||
                path.startsWith("/v3api-docs/**") ||
                path.startsWith("/swagger-ui/**") ||
                path.startsWith("/swagger-ui.html");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            try {
                jwtService.validateToken(token, TokenEnum.ACCESS);
                String username = jwtService.extractUsername(token, TokenEnum.ACCESS);
                Set<String> roles = jwtService.extractUserFromToken(token, TokenEnum.ACCESS);
                UserDetails userDetails = customUserDetailService.loadUserByUsername(username);

                List<GrantedAuthority> authorities = roles.stream()
                        .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                log.info("JWT roles claim: {}", authorities);

                UsernamePasswordAuthenticationToken usernameToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null,authorities);

                usernameToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernameToken);
            }catch (ExpiredJwtTokenException exception){
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("""
                {
                  "error": "UNAUTHORIZED",
                  "message": "Access token is expired"
                }
                """);
                return;
            }

        }
        filterChain.doFilter(request, response);
    }
}

package morago.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import morago.customExceptions.token.ExpiredJwtTokenException;
import morago.enums.TokenEnum;
import morago.model.Role;
import morago.model.User;
import morago.security.CustomUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JWTService {
    private static final long CLOCK_SEC = 60;

    private final JwtProperties jwtProperties;

    public JWTService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    private SecretKey generateAccessKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getAccessSecret().getBytes());
    }
    private SecretKey generateRefreshKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getRefreshSecret().getBytes());
    }
    private SecretKey getSignKey(TokenEnum token) {
        return (token ==  TokenEnum.ACCESS) ? generateAccessKey() : generateRefreshKey();
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .issuer(jwtProperties.getIssuer())
                .subject(user.getPhoneNumber())
                .claim("userId", user.getId())
                .claim("roles", user.getRoles()
                        .stream()
                        .map(role -> role.getName())
                        .toList())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenExpireTime()))
                .signWith(generateAccessKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh_token");
        claims.put("jti", UUID.randomUUID().toString());
        return Jwts.builder()
                .claims(claims)
                .issuer(jwtProperties.getIssuer())
                .subject(user.getPhoneNumber())
                .claim("userId", user.getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshTokenExpireTime()))
                .signWith(generateRefreshKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String extractUsername(String token, TokenEnum tokenType) {
        return tokenParse(token, tokenType).getSubject();
    }

    public Long extractUserId(String token, TokenEnum tokenType) {
        Claims claims = tokenParse(token, tokenType);
        return claims.get("userId", Long.class);
    }

    public Set<String> extractUserFromToken(String token, TokenEnum tokenType) {
        Object st = tokenParse(token, tokenType).get("roles", List.class);
        if(st instanceof List<?>list) {
            return list.stream().filter(String.class::isInstance).map(String.class::cast).collect(Collectors.toSet());
        }
        return Set.of();
    }

    public void validateToken(String token, TokenEnum tokenType) {
        tokenParse(token, tokenType);
    }

    private Claims tokenParse(String token, TokenEnum tokenType){
        try{
            Claims claims = Jwts.parser()
                    .requireIssuer(jwtProperties.getIssuer())
                    .clockSkewSeconds(CLOCK_SEC)
                    .verifyWith(getSignKey(tokenType))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String type = claims.get("type", String.class);

            return claims;

        }catch (ExpiredJwtException e){
            throw new ExpiredJwtTokenException();
        }
    }

    public Instant getExpInstant(String token, TokenEnum tokenType) {
        Claims claims = tokenParse(token, tokenType);
        Date exp = claims.getExpiration();
        return exp.toInstant();
    }

   /* private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }*/

    /*private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }*/
}

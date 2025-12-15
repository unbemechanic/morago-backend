package morago.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import morago.customExceptions.ExpiredJwtTokenException;
import morago.enums.TokenEnum;
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

    public String generateToken(CustomUserDetails user) {
        return Jwts.builder()
                .issuer(jwtProperties.getIssuer())
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .claim("authorities", user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenExpireTime()))
                .signWith(generateAccessKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(CustomUserDetails user) {
        return Jwts.builder()
                .issuer(jwtProperties.getIssuer())
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshTokenExpireTime()))
                .signWith(generateRefreshKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String extractUsername(String token, TokenEnum tokenType) {
        return tokenParse(token, tokenType).getSubject();
    }
    public Set<String> extractUserFromToken(String token, TokenEnum tokenType) {
        Object st = tokenParse(token, tokenType).get("roles");
        if(st instanceof List<?>list) {
            return list.stream().filter(String.class::isInstance).map(String.class::cast).collect(Collectors.toSet());
        }
        return Set.of();
    }

    /*private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }*/

    /*private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecret())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }*/

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
            /*if (type == null || !type.equalsIgnoreCase(tokenType.name())) {
                throw new InvalidJwtTokenException();
            }*/
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

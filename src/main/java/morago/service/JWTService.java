package morago.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTService {
    private String jwtSecretKey = "";
    public JWTService(){
        try{
            KeyGenerator kg = KeyGenerator.getInstance("HmacSHA256");
            SecretKey secretKey = kg.generateKey();
            jwtSecretKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        }catch (NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }
    public String generateToken(String username) {
//        Map<String, Object> claims = (Map<String, Object>) authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 20))
                .and()
                .signWith(getSecret())
                .compact();
    }

    private Key getSecret() {
        byte[] keyBytes = jwtSecretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

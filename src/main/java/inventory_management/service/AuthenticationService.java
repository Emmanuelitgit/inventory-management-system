package inventory_management.service;

import inventory_management.exception.UnAuthorizeException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class AuthenticationService {

    String SECRET = "RKUGLRKBKBSKLGSFIJSBKFBKJSDJBVugdtyidvctyfktvgkuyrcggchvrydtxtxuvyvgghghhhjhkjkjjurtyvkgvK";
    long EXPIRATION_TIME = TimeUnit.DAYS.toMillis(365);

    /**
     * @description: A method to generate a jwt token
     * @auther
     * @param: username
     * @return token
     * @createdAt 29th, May 2025
     */
    public String generateToken(String username, UUID userId){
        Map<String, Object> claims = new HashMap<>();
        claims.put("issuer", "www.emma.com");
        claims.put("userId", userId);
        return Jwts.builder()
                .setClaims(claims)
                .signWith(secretKey())
                .setExpiration(Date.from(Instant.now().plusMillis(EXPIRATION_TIME)))
                .setIssuedAt(Date.from(Instant.now()))
                .setSubject(username)
                .compact();
    }

    /**
     * @description: A method to extract user details or claims from a token
     * @auther
     * @param: token
     * @return claims such as email, username,authorities etc
     * @createdAt 29th, May 2025
     */
    public Claims getClaims(String token) {
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new UnAuthorizeException("Invalid token or signature");
        }
    }

    /**
     * @description: A method to extract username from a token
     * @auther
     * @param: token
     * @return username
     * @createdAt 29th, May 2025
     */
    public String extractUsername(String token){
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    public Object extractUserId(String token){
        Claims claims = getClaims(token);
        return claims.get("userId");
    }

    /**
     * @description: A method to check if token is valid
     * @auther
     * @param: token
     * @return boolean
     * @createdAt 29th, May 2025
     */
    public boolean  isTokenValid(String token){
        Claims claims = getClaims(token);
        return Date.from(Instant.now()).before(claims.getExpiration());
    }

    /**
     * @description: A method to generate a secret key for token generation and verification
     * @auther
     * @return SecretKey
     * @createdAt 29th, May 2025
     */
    private SecretKey secretKey(){
        byte[] decodedKey = Base64.getDecoder().decode(SECRET);
        return Keys.hmacShaKeyFor(decodedKey);
    }
}

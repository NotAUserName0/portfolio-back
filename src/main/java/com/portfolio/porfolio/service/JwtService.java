package com.portfolio.porfolio.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.portfolio.porfolio.models.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    @Value("${admin.default.secretKey}")
    private String secretKey;

    @Value("${admin.default.expiration}")
    private Long expiration;

    /**
     * @description Generates a JWT token with the provided claims and user information.
     * @param claims The claims to include in the token.
     * @param user The user for whom the token is generated.
     * @return The generated JWT token.
     */
    public String generateToken(Map<String, Object> claims, User user) {
        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * @description Obtains the signing key as a SecretKey.
     * @return The SecretKey used for signing the JWT.
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * @description Extracts all claims from the token using the new parser() and verifyWith() methods.
     * @param token The JWT token from which to extract claims.
     * @return The claims contained in the token.
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * @description Checks if the provided token is valid for the given user.
     * @param token The JWT token to validate.
     * @param user The user against whom the token is validated.
     * @return True if the token is valid, false otherwise.
     */
    public Boolean isTokenValid(String token, User user) {
        final String username = extractUsername(token);
        return (username.equals(user.getUsername())) && !isTokenExpired(token);
    }

    /**
     * @description Extracts the username (subject) from the token.
     * @param token The JWT token from which to extract the username.
     * @return The username contained in the token.
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * @description Checks if the token has expired.
     * @param token The JWT token to check.
     * @return True if the token has expired, false otherwise.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * @description Extracts the expiration date from the token.
     * @param token The JWT token from which to extract the expiration date.
     * @return The expiration date of the token.
     */
    private Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }
}

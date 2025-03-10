package com.beta.schoolpayment.util;

import com.beta.schoolpayment.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    // Generate SecretKey using Keys.hmacShaKeyFor
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Extract username (subject) from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract specific claim from token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims using parserBuilder
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if the token is expired
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    // Generate token with custom claims
    public String generateToken(User user) {
        return Jwts.builder()
                .claim("role", user.getRole())   // Role pengguna
                .claim("userId", user.getUserId()) // ID user dalam database
                .claim("name", user.getName())  // Nama lengkap pengguna
                .claim("email", user.getEmail()) // Email pengguna
                .claim("nis", user.getNis())   // NIS pengguna
                .setSubject(user.getEmail() != null ? user.getEmail() : user.getNis().toString()) // Gunakan Email jika ada, jika tidak pakai NIS
                .setIssuedAt(new Date()) // Waktu pembuatan token
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Berlaku 10 jam
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    // Validate the token
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}

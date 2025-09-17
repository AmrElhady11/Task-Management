package com.TaskApp.service.Impl;


import com.TaskApp.repository.TokenRepository;
import com.TaskApp.repository.UserRepository;
import com.TaskApp.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {
        private final String SECRET_KEY = "a3f5c1b9e8d7425f9c6a1e4b7d3f8a0c9e2b4d1f6a7c3e8b5d9f0a2c7e4b8d1";

        private final UserRepository userRepository;
        private final TokenRepository tokenRepository;
        @Autowired
        public JwtServiceImpl(TokenRepository tokenRepository, UserRepository userRepository) {
            this.tokenRepository = tokenRepository;
            this.userRepository = userRepository;
        }
    @Override
    public String extractUserName(String token) {

            return extractClaim(token, s-> s.getSubject());
    }
    @Override
    public String generateToken(UserDetails userDetails) {

            return generateToken(new HashMap<>(),userDetails);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername())&& !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {

            return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {

            return extractClaim(token,s->s.getExpiration());
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

    }

    private  <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
        final Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}

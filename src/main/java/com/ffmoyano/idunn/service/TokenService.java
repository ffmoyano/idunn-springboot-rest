package com.ffmoyano.idunn.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.ffmoyano.idunn.dto.TokenResponse;
import com.ffmoyano.idunn.entity.Token;
import com.ffmoyano.idunn.entity.AppUser;
import com.ffmoyano.idunn.repository.TokenRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class TokenService {

    @Value("${jwtSecret}")
    private String jwtSecret;

    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public TokenResponse generateTokens(User user, String url) {
        String jwtToken = generateJwt(user.getUsername(), user.getAuthorities(), url);
        String refreshToken = generateRefreshToken();
        return new TokenResponse(jwtToken, refreshToken);
    }

    public TokenResponse generateTokens(AppUser user, String url) {
        var authorities = new ArrayList<GrantedAuthority>();
        user.getRoles().forEach(r -> authorities.add(new SimpleGrantedAuthority(r.getName())));
        String jwtToken = generateJwt(user.getEmail(), authorities, url);
        String refreshToken = generateRefreshToken();
        return new TokenResponse(jwtToken, refreshToken);
    }

    private String generateJwt(String email, Collection<GrantedAuthority> authorities, String url) {

        Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return JWT.create()
                .withSubject(email)
                // 30 minutes
                .withExpiresAt(new Date(System.currentTimeMillis() + (30 * 60 * 1000)))
                .withIssuer(url)
                .withClaim("roles", authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .sign(algorithm);
    }

    private String generateRefreshToken() {
        return RandomStringUtils.randomAlphanumeric(26);
    }


    public Token save(Token token) {
        return tokenRepository.save(token);
    }


    public Token findTokenByUser(AppUser user) {
        return tokenRepository.findTokenByUser(user);
    }

    public Token findTokenByRefreshToken(String refreshToken) {
        return tokenRepository.findTokenByRefreshToken(refreshToken);
    }

}

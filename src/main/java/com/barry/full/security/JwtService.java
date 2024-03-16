package com.barry.full.security;

import com.barry.full.entity.Jwt;
import com.barry.full.entity.User;
import com.barry.full.repository.JwtRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@Slf4j
public class JwtService {

    private final String ENCRYPTION_KEY = "db9bb04822c65616fb577456947dccff94eca1c3b203317bb9b69e8c5a844d34";
    public String extractUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }
    @Autowired
    private JwtRepository jwtRepository;
    private <T> T getClaim(String token, Function<Claims, T> function) {
        Claims claims = getAllClaims(token);
        return function.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    private SecretKey getKey() {
        byte[] decoder = Decoders.BASE64.decode(ENCRYPTION_KEY);
        return Keys.hmacShaKeyFor(decoder);
    }


    public boolean isTokenExpired(String token) {
        Date expiration = extractExpirationFromToken(token);
        return expiration.before(new Date());
    }

    private Date extractExpirationFromToken(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    public Map<String, String> generateToken(String username) {
        return this.generateJwt(username);
    }

    private Map<String, String> generateJwt(String username) {
        long exp = System.currentTimeMillis() + 24* 60 * 60 * 1000;
        Date issuedAt = new Date();
        Date expireIn = new Date(exp);

        Map<String, Object> claims = Map.of(
                "name",  username,
                Claims.EXPIRATION, expireIn,
                Claims.SUBJECT, username
        );

        final String bearer = Jwts.builder()
                .claims().add(claims)
                .issuedAt(issuedAt)
                .expiration(expireIn)
                .subject(username)
                .and()
                .signWith(getKey())
                .compact();

        return Map.of("bearer", bearer);


    }

    public void saveJwt(Jwt jwt) {
        this.jwtRepository.save(jwt);
    }

    public Jwt findBytoken(String token) {
        return this.jwtRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Token not found "));
    }

    public void expireUserTokens(User user) {
        List<Jwt> userTokens = this.jwtRepository.getUserTokens(user.getUsername()).peek(
                jwt -> jwt.setExpire(true)
        ).collect(Collectors.toList());

        this.jwtRepository.saveAll(userTokens);

    }

    public String logout() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Jwt jwt = this.jwtRepository.findOneByUserAndExpire(user, false).orElseThrow(() -> new RuntimeException("No token found "));
        jwt.setExpire(true);
        this.jwtRepository.save(jwt);
        return "Logout ok ";
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void pruneTokens(){

        this.jwtRepository.deleteAllByExpire(true);
        log.info("every minute prune ");
    }
}

package com.example.oauthjwt.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * jwt 발급 및 파싱 클래스
 */
@Component
public class JWTUtil {

    private SecretKey secretKey;

    /**
     * HS256 방식 암호화 키 생성
     */
    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    /**
     * username
     */
    public String getUsername(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);
    }

    /**
     * role
     */
    public String getRole(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    /**
     * 만료여부
     */
    public Boolean isExpired(String token) {
        try{
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration()
                    .before(new Date());

        }catch (ExpiredJwtException e) {
            // 만료된 JWT를 파싱할 때 ExpiredJwtException이 발생
            return true;
        } catch (Exception e) {
            // 다른 에러의 경우, 예외를 다시 던지거나 로그를 기록할 수 있습니다.
            throw new RuntimeException("JWT 검증 중 오류 발생", e);
        }
    }

    /**
     * jwt 토큰 생성
     * 페이로드 내용 (Payload)
     * username, role, 생성일, 만료일
     * @param username
     * @param role
     * @param expiredMs 토큰 만료일
     * @return jwt 토큰 반환
     */
    public String createJwt(String username, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }
}
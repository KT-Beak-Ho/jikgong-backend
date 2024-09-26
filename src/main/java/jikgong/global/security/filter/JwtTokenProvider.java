package jikgong.global.security.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtTokenProvider {

    private static final Long ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 120L; // 120 days
    //    private static final Long ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 2L; // 2 hours
    private static final Long REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 90L; // 90 days
    private final RedisTemplate<String, String> redisTemplate;
    private final Key key;

    @Autowired
    public JwtTokenProvider(@Value("${app.auth.secret-key}") String secretKey,
        RedisTemplate<String, String> redisTemplate) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.redisTemplate = redisTemplate;
    }

    public String createAccessToken(String loginId) {
        Map<String, Object> claim = new HashMap<>();
        // 토큰에 들어갈 정보 세팅
        claim.put("loginId", loginId); // 사용자 휴대폰 번호
        return createJwt("ACCESS_TOKEN", ACCESS_TOKEN_EXPIRATION_TIME, claim);
    }

    public String createRefreshToken(String loginId) {
        HashMap<String, Object> claim = new HashMap<>();
        claim.put("loginId", loginId); // 사용자 휴대폰 번호
        String refreshToken = createJwt("REFRESH_TOKEN", REFRESH_TOKEN_EXPIRATION_TIME, claim);
        saveRefreshTokenInRedis(loginId, refreshToken);
        return refreshToken;
    }

    public String createJwt(String subject, Long expiration, Map<String, Object> claim) {
        JwtBuilder jwtBuilder = Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setSubject(subject)
            .setIssuedAt(new Date())
            .signWith(key, SignatureAlgorithm.HS256);

        // claim 세팅
        if (claim != null) {
            jwtBuilder.setClaims(claim);
        }

        // 만료 기한 설정
        if (expiration != null) {
            jwtBuilder.setExpiration(new Date(new Date().getTime() + expiration));
        }

        return jwtBuilder.compact();
    }

    /**
     * 복호화
     */
    public Claims get(String jwt) throws JwtException {
        return Jwts
            .parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(jwt)
            .getBody();
    }

    /**
     * 토큰 만료 여부 체크
     * @return true : 만료됨, false : 만료되지 않음
     */
    public boolean isExpiration(String jwt) throws JwtException {
        log.info("토큰 만료 여부 체크");
        try {
            return get(jwt).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public void saveRefreshTokenInRedis(String loginId, String refreshToken) {
        // redis 에 저장
        redisTemplate.opsForValue().set(
            loginId,
            refreshToken,
            REFRESH_TOKEN_EXPIRATION_TIME,
            TimeUnit.MILLISECONDS
        );
        log.info("redis 에 refresh token 저장");
    }
}

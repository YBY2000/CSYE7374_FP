package com.project.platform.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.entity.Admin;
import com.project.platform.service.AdminService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to generate and verify JWT tokens.
 */
@Slf4j
public class JwtUtils {

    @Resource
    private static AdminService userService;

    /**
     * Token expiration time in milliseconds.
     * This value represents 30 days.
     */
    private static final long TOKEN_EXPIRED_TIME = 30L * 24 * 60 * 60 * 1000;

    public static final String jwtId = "tokenId";

    /**
     * Secret key used for JWT encryption/decryption (can be customized).
     */
    private static final String JWT_SECRET = "1234567890";

    /**
     * Create a JWT token.
     */
    public static String createJWT(Map<String, Object> claims, Long time) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256; // Specify the signing algorithm to use (already handled by jjwt).
        Date now = new Date(System.currentTimeMillis());

        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis(); // Current time (for issued-at claim)

        // Add standard and custom claims to the payload
        JwtBuilder builder = Jwts.builder()              // Create a JwtBuilder to build the token body
                .setClaims(claims)                       // Set custom claims first
                .setId(jwtId)                            // Set the JWT ID (jti) as a unique identifier to prevent replay attacks
                .setIssuedAt(now)                        // Set the issued-at time (iat)
                .signWith(signatureAlgorithm, secretKey); // Sign with algorithm and secret key

        if (time >= 0) {
            long expMillis = nowMillis + time;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);                 // Set the token expiration time
        }
        return builder.compact();
    }

    /**
     * Verify a JWT token.
     */
    public static Claims verifyJwt(String token) {
        // Use the same secret key that was used to generate the token
        SecretKey key = generalKey();
        Claims claims;
        try {
            claims = Jwts.parser()            // Get the DefaultJwtParser
                    .setSigningKey(key)       // Set the secret key for verification
                    .parseClaimsJws(token)    // Parse the JWT
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    /**
     * Generate a secret key from the string.
     *
     * @return SecretKey
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.getEncoder().encode(JWT_SECRET.getBytes());
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

    /**
     * Generate a token using userId and openid.
     * Accepts a CurrentUserDTO object.
     */
    public static String generateToken(CurrentUserDTO currentUserDTO) {
        Map<String, Object> map = new HashMap<>();
        map.put("currentUser", JSON.toJSONString(currentUserDTO));
        return createJWT(map, TOKEN_EXPIRED_TIME);
    }
}

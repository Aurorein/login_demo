package com.haoweilai.demo1.util;

import io.jsonwebtoken.*;
import org.joda.time.DateTime;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtUtils {

    public static final String JWT_KEY_USERID = "userId";
    public static final String JWT_KEY_ORIGIN = "origin";
    public static final String JWT_KEY_ACCESS = "access";
    public static final String JWT_KEY_REFRESH = "refresh";
    public static final int EXPIRE_MINUTES = 600;

    /**
     * 私钥加密token
     */
    public static String generateAccessToken(String userId, String origin, PrivateKey privateKey, int expireMinute) {
        return generateToken(userId, origin, JWT_KEY_ACCESS, privateKey, expireMinute);
    }

    public static String generateRefreshToken(String userId, String origin, PrivateKey privateKey, int expireMinute) {
        return generateToken(userId, origin, JWT_KEY_REFRESH, privateKey, expireMinute);
    }

    public static String generateToken(String userId, String origin, String type, PrivateKey privateKey, int expireMinutes) {

        return Jwts.builder()
                .claim(JWT_KEY_USERID, userId)
                .claim(JWT_KEY_ORIGIN, origin)
                .claim(JWT_KEY_ACCESS, type)
                .setExpiration(DateTime.now().plusMinutes(expireMinutes).toDate())
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }

    /**
     * 从token解析用户
     *
     * @param token
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String getUsernameFromToken(String token, PublicKey publicKey){
        String userId = "";
        Claims body = null;
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
            body = claimsJws.getBody();
        }catch (ExpiredJwtException e){
            body = e.getClaims();
        }
        userId = (String) body.get(JWT_KEY_USERID);
        return userId;
    }

    public static String getOriginFromToken(String token, PublicKey publicKey){
        String userId = "";
        Claims body = null;
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
            body = claimsJws.getBody();
        }catch (ExpiredJwtException e){
            body = e.getClaims();
        }
        userId = (String) body.get(JWT_KEY_ORIGIN);
        return userId;
    }

}


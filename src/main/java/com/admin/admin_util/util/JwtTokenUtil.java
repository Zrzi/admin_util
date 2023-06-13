package com.admin.admin_util.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author 陈群矜
 */
public class JwtTokenUtil {

    private static final String CLAIM_KEY_USER_NO = "userNo";
    private static final String CLAIM_KEY_USERNAME = "username";
    private static final String CLAIM_KEY_USER_TYPE = "userType";
    private static final String SECRET = "secret";
    private static final String ISSUER = "sdu-admin";

    public String getUserNoFromToken(String token) {
        String userNo;
        try {
            final Claims claims = getClaimsFromToken(token);
            userNo = claims.get(CLAIM_KEY_USER_NO, String.class);
        } catch (Exception e) {
            userNo = null;
        }
        return userNo;
    }

    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = getClaimsFromToken(token);
            username = claims.get(CLAIM_KEY_USERNAME, String.class);
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    public String getUserTypeFromToken(String token) {
        // 返回结果可能是 系统用户、学生、教师
        String userType = null;
        try {
            final Claims claims = getClaimsFromToken(token);
            userType = claims.get(CLAIM_KEY_USER_TYPE, String.class);
        } catch (Exception ignored) {

        }
        return userType;
    }

    public Date getCreatedDateFromToken(String token) {
        Date created;
        try {
            final Claims claims = getClaimsFromToken(token);
            created = claims.getIssuedAt();
        } catch (Exception e) {
            created = null;
        }
        return created;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts
                    .parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public boolean validateToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return false;
        }
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .requireIssuer(ISSUER)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception ignored) {
            return false;
        }
        return !isTokenExpired(token);
    }

}

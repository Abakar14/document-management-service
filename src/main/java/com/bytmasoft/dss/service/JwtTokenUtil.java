package com.bytmasoft.dss.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JwtTokenUtil {


    private static String secret = "8KvVu4Bn2KwBNX1MdnXY6VaRVLekVxurk3aaJxnzdCuZL6AHEL0EnnAOYLZtHWUr";
    private static Long accessTokenExpiration = 1800000L;

    public static String generateToken(String username, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

}

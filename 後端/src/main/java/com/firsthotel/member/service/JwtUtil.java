package com.firsthotel.member.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;

import java.util.Base64;
import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class JwtUtil {

    // 密钥是 Base64 编码的字符串
    private String secretKey = "dGhpcyBpcyBhIHNlY3VyZSBzZWN1cmUga2V5IHRoYXQgY2Fubm90IGJlIHVzZWQgd2hlbiB5b3UgY3JlYXRlIGVuY29kaW5nIG1ldGFkYXRh";  

    // 解码密钥
    private byte[] decodedSecretKey() {
        return Base64.getDecoder().decode(secretKey);  // 解码为字节数组
    }

    // 生成 JWT
    public String generateToken(String username,String role) {
        return Jwts.builder()
                .setSubject(username) // 设置主题
                .claim("role", role)
                .setIssuedAt(new Date())  // 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))  // 设置过期时间，这里是 10 小时
                .signWith(SignatureAlgorithm.HS256, decodedSecretKey())  // 使用解码后的字节密钥
                .compact();
    }

    // 解析 JWT
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(decodedSecretKey())  // 使用解码后的字节密钥
                .parseClaimsJws(token)
                .getBody();
    }

    // 从 JWT 中提取用户名
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // 检查 JWT 是否过期
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // 验证 JWT
    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }

	
}

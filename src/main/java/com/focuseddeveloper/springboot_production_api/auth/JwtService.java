package com.focuseddeveloper.springboot_production_api.auth;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.springframework.stereotype.Service;

import com.focuseddeveloper.springboot_production_api.config.JwtProperties;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
  private final JwtProperties props;
  private final Key key;

  public JwtService(JwtProperties props) {
    this.props = props;
    byte[] bytes = props.getSecret().getBytes(StandardCharsets.UTF_8);
    this.key = Keys.hmacShaKeyFor(bytes);
  }

  public String generate(UUID userId, String email, Collection<String> roles) {
    Instant now = Instant.now();
    return Jwts.builder()
      .setSubject(email)
      .claim("uid", userId.toString())
      .claim("roles", roles)
      .setIssuer(props.getIssuer())
      .setIssuedAt(Date.from(now))
      .setExpiration(Date.from(now.plus(props.getAccessTokenMinutes(), ChronoUnit.MINUTES)))
      .signWith(key, SignatureAlgorithm.HS256)
      .compact();
  }

  public Claims verify(String token) {
    return Jwts.parserBuilder().requireIssuer(props.getIssuer()).setSigningKey(key).build()
      .parseClaimsJws(token).getBody();
  }
}

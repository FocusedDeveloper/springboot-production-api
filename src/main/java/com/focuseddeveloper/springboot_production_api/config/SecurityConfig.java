package com.focuseddeveloper.springboot_production_api.config;

import com.focuseddeveloper.springboot_production_api.auth.JwtService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProperties.class)
@RequiredArgsConstructor
public class SecurityConfig {
  private final JwtService jwt;

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
      .csrf(csrf -> csrf.disable())
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/auth/register","/api/auth/login", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                         "/actuator/health/**").permitAll()
        .anyRequest().authenticated())
      .exceptionHandling(ex -> ex
        .authenticationEntryPoint((req,res,e)->json(res,401,"Unauthorized","Missing or invalid token",req.getRequestURI()))
        .accessDeniedHandler((req,res,e)->json(res,403,"Forbidden","Access is denied",req.getRequestURI())))
      .addFilterBefore(new JwtFilter(jwt), UsernamePasswordAuthenticationFilter.class)
      .build();
  }

  @Bean PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }

  static class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwt;
    JwtFilter(JwtService jwt){ this.jwt = jwt; }
    @Override protected void doFilterInternal(@NonNull HttpServletRequest req,@NonNull HttpServletResponse res,@NonNull FilterChain chain)
        throws ServletException, IOException {
      String h = req.getHeader("Authorization");
      if (h != null && h.startsWith("Bearer ")) {
        try {
          var c = jwt.verify(h.substring(7));
          var roles = ((List<?>) c.get("roles")).stream().map(String::valueOf).toList();
          var auth = new UsernamePasswordAuthenticationToken(
              new JwtPrincipal(UUID.fromString((String)c.get("uid")), c.getSubject(), roles),
              null,
              roles.stream().map(SimpleGrantedAuthority::new).toList());
          SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (JwtException e) { SecurityContextHolder.clearContext(); }
      }
      chain.doFilter(req, res);
    }
  }

  public record JwtPrincipal(UUID userId, String email, List<String> roles) {}

  private static void json(HttpServletResponse res, int status, String title, String detail, String path) throws IOException {
    res.setStatus(status); res.setContentType(MediaType.APPLICATION_JSON_VALUE);
    res.getWriter().write("{\"title\":\""+title+"\",\"status\":"+status+",\"detail\":\""+detail+"\",\"path\":\""+path+"\"}");
  }
}

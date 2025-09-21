package com.focuseddeveloper.springboot_production_api.auth;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import com.focuseddeveloper.springboot_production_api.config.SecurityConfig;

public final class CurrentUserHelper {
  private CurrentUserHelper() {}

  public static UUID getCurrentUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
      throw new ResponseStatusException(HttpStatusCode.valueOf(401), "Unauthenticated");
    }
    Object p = auth.getPrincipal();
    if (p instanceof SecurityConfig.JwtPrincipal jp) {
      return jp.userId();
    }
    throw new IllegalStateException("Unsupported principal: " + p.getClass().getName());
  }

  public static String getCurrentEmail() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
      throw new ResponseStatusException(HttpStatusCode.valueOf(401), "Unauthenticated");
    }
    Object p = auth.getPrincipal();
    if (p instanceof SecurityConfig.JwtPrincipal jp) {
      return jp.email();
    }
    throw new IllegalStateException("Unsupported principal: " + p.getClass().getName());
  }

  public static List<String> getCurrentRoles() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || auth.getAuthorities() == null) {
      throw new ResponseStatusException(HttpStatusCode.valueOf(401), "Unauthenticated");
    }
    return auth.getAuthorities().stream().map(a -> a.getAuthority()).toList();
  } 
}

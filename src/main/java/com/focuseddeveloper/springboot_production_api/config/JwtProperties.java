package com.focuseddeveloper.springboot_production_api.config;

import jakarta.validation.constraints.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
  @NotBlank private String secret;
  @NotBlank private String issuer;
  @Min(1) @Max(24 * 60) private long accessTokenMinutes;

  public String getSecret(){ return secret; }
  public void setSecret(String s){ this.secret = s; }
  public String getIssuer(){ return issuer; }
  public void setIssuer(String s){ this.issuer = s; }
  public long getAccessTokenMinutes(){ return accessTokenMinutes; }
  public void setAccessTokenMinutes(long m){ this.accessTokenMinutes = m; }
}
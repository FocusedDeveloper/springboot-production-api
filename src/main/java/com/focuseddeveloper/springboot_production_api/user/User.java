package com.focuseddeveloper.springboot_production_api.user;

import java.time.Instant;
import java.util.*;

import jakarta.persistence.*;

@Entity @Table(name="users")
public class User {
  @Id private UUID id = UUID.randomUUID();

  @Column(nullable=false, unique=true, length=255)
  private String email;

  @Column(nullable=false, length=255)
  private String password; // BCrypt

  @Column(nullable=false, length=255)
  private String roles; // e.g., "ROLE_STUDENT,ROLE_TEACHER"

  @Column(nullable=false, length=255)
  private String fullName;

  @Column(nullable=false)
  private Instant createdAt = Instant.now();

  public User() {
    // JPA requires a no-arg constructor
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRoles() {
    return roles;
  }

  public void setRoles(String roles) {
    this.roles = roles;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  @Transient
  public List<String> getRoleList() {
    return Arrays.stream(roles.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
  }
}

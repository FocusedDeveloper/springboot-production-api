package com.focuseddeveloper.springboot_production_api.user;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service @RequiredArgsConstructor
public class UserService {
  private final UserRepository repo;
  private final PasswordEncoder encoder;

  @Transactional
  public User registerStudent(String email, String rawPassword, String fullName) {
    if (repo.existsByEmail(email)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
    User u = new User();
    u.setEmail(email);
    u.setPassword(encoder.encode(rawPassword));
    u.setRoles("ROLE_STUDENT");
    u.setFullName(fullName);
    return repo.save(u);
  }

  public User authenticate(String email, String rawPassword) {
    var u = repo.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
    if (!encoder.matches(rawPassword, u.getPassword()))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    return u;
  }
}

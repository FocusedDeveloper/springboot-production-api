package com.focuseddeveloper.springboot_production_api.auth;

import com.focuseddeveloper.springboot_production_api.user.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final UserService users;
  private final JwtService jwt;

  public record RegisterReq(@Email @NotBlank String email, @NotBlank String password, @NotBlank String fullName) {}
  public record LoginReq(@Email @NotBlank String email, @NotBlank String password) {}
  public record TokenRes(String accessToken) {}

  @PostMapping("/register")
  public ResponseEntity<TokenRes> register(@Valid @RequestBody RegisterReq req) {
    var u = users.registerStudent(req.email(), req.password(), req.fullName());
    var token = jwt.generate(u.getId(), u.getEmail(), List.of("ROLE_STUDENT"));
    return ResponseEntity.status(HttpStatus.CREATED).body(new TokenRes(token));
  }

  @PostMapping("/login")
  public TokenRes login(@Valid @RequestBody LoginReq req) {
    var u = users.authenticate(req.email(), req.password());
    return new TokenRes(jwt.generate(u.getId(), u.getEmail(), u.getRoleList()));
  }
}
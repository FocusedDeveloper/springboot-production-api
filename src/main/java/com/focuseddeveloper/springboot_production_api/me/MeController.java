package com.focuseddeveloper.springboot_production_api.me;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.focuseddeveloper.springboot_production_api.auth.CurrentUserHelper;
import com.focuseddeveloper.springboot_production_api.user.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MeController {

    public record WhoAmI(UUID id, String email, String[] roles, String fullName) {}

    private final UserRepository userRepository;

    @Operation(summary = "Return the current authenticated user",
           security = @SecurityRequirement(name = "bearer-jwt"))
    @GetMapping("/me")
    public WhoAmI whoAmI() {

        UUID userId = CurrentUserHelper.getCurrentUserId();
        String email = CurrentUserHelper.getCurrentEmail();
        List<String> roles = CurrentUserHelper.getCurrentRoles();
        var user = userRepository.findById(userId).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found for token"));

        return new WhoAmI(
            userId,
            email,
            roles.toArray(new String[0]),
            user.getFullName()
        );
        

    }

}
package fu.he182575.rwm_backend.controller;

import fu.he182575.rwm_backend.dto.LoginRequest;
import fu.he182575.rwm_backend.dto.LoginResponse;
import fu.he182575.rwm_backend.dto.UserSummaryResponse;
import fu.he182575.rwm_backend.service.AuthService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Login and security endpoints")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login with ADMIN or STAFF credentials")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login success",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "403", description = "Role not allowed")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Get the current authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Current authenticated user",
                    content = @Content(schema = @Schema(implementation = UserSummaryResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing, invalid, expired, or disabled account token")
    })
    public ResponseEntity<UserSummaryResponse> me(@AuthenticationPrincipal UUID userId) {
        return ResponseEntity.ok(authService.currentUser(userId));
    }
}

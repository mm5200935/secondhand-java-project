package app.controller;

import app.dto.request.LoginRequest;
import app.dto.request.RegisterRequest;
import app.dto.response.AuthResponse;
import app.dto.response.UserResponse;
import app.model.User;
import app.security.AuthenticatedUser;
import app.security.JwtUtil;
import app.service.interfaces.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // ================= REGISTER =================

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        User saved = userService.register(user);

        return buildAuthResponse(saved);
    }

    // ================= LOGIN =================

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {

        User user = userService.login(request.getUsername(), request.getPassword());

        return buildAuthResponse(user);
    }

    // ================= CURRENT USER (protected) =================

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal AuthenticatedUser principal) {

        User user = userService.findById(principal.id());

        return map(user);
    }

    // ================= LIST ALL USERS (admin only) =================

    @GetMapping
    public List<UserResponse> getAll(@AuthenticationPrincipal AuthenticatedUser principal) {

        requireAdmin(principal);

        return userService.getAllUsers()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    // ================= HELPERS =================

    private void requireAdmin(AuthenticatedUser principal) {
        if (!"ADMIN".equals(principal.role())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admins only.");
        }
    }

    private AuthResponse buildAuthResponse(User user) {

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getUsername(),
                user.getRole().name());

        return new AuthResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole().name());
    }

    private UserResponse map(User user) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setRole(user.getRole().name());
        response.setStatus(user.getStatus().name());
        response.setAverageRating(user.getAverageRating());

        return response;
    }

    // ================= BLOCK / UNBLOCK (admin only) =================

    @PutMapping("/{id}/block")
    public UserResponse block(@PathVariable int id,
                              @AuthenticationPrincipal AuthenticatedUser principal) {
        requireAdmin(principal);
        User user = userService.blockUser(id);
        return map(user);
    }

    @PutMapping("/{id}/unblock")
    public UserResponse unblock(@PathVariable int id,
                                @AuthenticationPrincipal AuthenticatedUser principal) {
        requireAdmin(principal);
        User user = userService.unblockUser(id);
        return map(user);
    }
}
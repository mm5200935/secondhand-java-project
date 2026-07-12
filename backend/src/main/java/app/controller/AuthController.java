package app.controller;

import app.dto.AuthRequest;
import app.dto.AuthResponse;
import app.dto.RegisterRequest;
import app.enums.UserRole;
import app.model.User;
import app.security.JwtUtil;
import app.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setPassword(request.getPassword());
            newUser.setFullName(request.getFullName());
            newUser.setEmail(request.getEmail());
            newUser.setPhone(request.getPhoneNumber());
            newUser.setRole(UserRole.USER); // نقش پیش‌فرض

            User registeredUser = userService.register(newUser);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            User user = userService.login(request.getUsername(), request.getPassword());
            if (user != null) {
                String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
                AuthResponse response = new AuthResponse(token, user.getId(), user.getUsername(), user.getRole());
                return ResponseEntity.ok(response);
            } else {
                return new ResponseEntity<>("نام کاربری یا رمز عبور اشتباه است.", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}
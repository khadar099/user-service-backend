package com.yourcompany.ecommerce.userservice.controller;

import com.yourcompany.ecommerce.userservice.entity.User;
import com.yourcompany.ecommerce.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        User createdUser = userService.register(user);
        return ResponseEntity.ok("User registered with id: " + createdUser.getId());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        User user = userService.login(loginData.get("username"), loginData.get("password"));
        return ResponseEntity.ok("Welcome " + user.getUsername() + " to the E-commerce site!");
    }
}

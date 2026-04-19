package com.example.sim_activation_portal.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.sim_activation_portal.entity.Customer;
import com.example.sim_activation_portal.service.CustomerService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    @Autowired
    private CustomerService customerService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username,
                                   @RequestParam String password,
                                   HttpSession session) {
        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            session.setAttribute("admin", true);
            session.setAttribute("username", username);

            // Set a default customer for any features that still rely on session (e.g., address update)
            Optional<Customer> defaultCustomer = customerService.getFirstCustomer();
            if (defaultCustomer.isPresent()) {
                session.setAttribute("customerId", defaultCustomer.get().getId());
                session.setAttribute("customerName", defaultCustomer.get().getFirstName());
            } else {
                session.setAttribute("customerId", 1L);
                session.setAttribute("customerName", "Demo");
            }

            return ResponseEntity.ok(Map.of("success", true, "message", "Login successful"));
        } else {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid username or password"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("success", true));
    }
}
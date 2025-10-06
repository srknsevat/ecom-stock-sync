package com.ornek.ecomstocksync.controller;

import com.ornek.ecomstocksync.entity.UserAccount;
import com.ornek.ecomstocksync.repository.UserAccountRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserAccountRepository repo;
    private final PasswordEncoder encoder;

    public AdminUserController(UserAccountRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @GetMapping
    public List<UserAccount> list() {
        return repo.findAll();
    }

    @PostMapping
    public ResponseEntity<UserAccount> create(@RequestBody UserAccount req) {
        if (repo.existsByUsername(req.getUsername())) {
            return ResponseEntity.badRequest().build();
        }
        req.setPassword(encoder.encode(req.getPassword()));
        if (req.getRole() == null || req.getRole().isBlank()) {
            req.setRole("ROLE_USER");
        }
        return ResponseEntity.ok(repo.save(req));
    }
}




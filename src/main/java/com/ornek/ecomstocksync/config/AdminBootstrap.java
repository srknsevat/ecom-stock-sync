package com.ornek.ecomstocksync.config;

import com.ornek.ecomstocksync.entity.UserAccount;
import com.ornek.ecomstocksync.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminBootstrap {

    @Bean
    public CommandLineRunner initAdmin(UserAccountRepository repo,
                                       PasswordEncoder encoder,
                                       @Value("${app.admin.username:admin}") String adminUser,
                                       @Value("${app.admin.password:admin}") String adminPass) {
        return args -> {
            System.out.println("=== ADMIN BOOTSTRAP ===");
            System.out.println("Admin username: " + adminUser);
            System.out.println("Admin password: " + adminPass);
            System.out.println("User exists: " + repo.existsByUsername(adminUser));
            
            if (!repo.existsByUsername(adminUser)) {
                UserAccount ua = new UserAccount();
                ua.setUsername(adminUser);
                ua.setPassword(encoder.encode(adminPass));
                ua.setRole("ROLE_ADMIN");
                ua.setEnabled(true);
                repo.save(ua);
                System.out.println("Admin user created successfully!");
            } else {
                System.out.println("Admin user already exists!");
            }
        };
    }
}



package com.ornek.ecomstocksync.security;

import com.ornek.ecomstocksync.repository.UserAccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountRepository repo;

    public CustomUserDetailsService(UserAccountRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var ua = repo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı"));
        return User.withUsername(ua.getUsername())
                .password(ua.getPassword())
                .roles(ua.getRole().replace("ROLE_", ""))
                .disabled(!ua.isEnabled())
                .build();
    }
}



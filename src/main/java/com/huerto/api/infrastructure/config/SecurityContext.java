package com.huerto.api.infrastructure.config;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityContext {

    public UUID getCurrentUserId() {
        String subject = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return UUID.fromString(subject);
    }

    public boolean isAdmin() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
package org.userservice.userservice.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.userservice.userservice.dto.auth.OAuth2UserDetails;

public class SecurityUtils {
    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return null;
        return ((OAuth2UserDetails) authentication.getPrincipal()).getProviderName();
    }
}

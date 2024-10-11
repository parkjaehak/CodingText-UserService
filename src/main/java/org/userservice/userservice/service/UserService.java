package org.userservice.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.userservice.userservice.domain.AuthRole;
import org.userservice.userservice.domain.User;
import org.userservice.userservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public void updateUserRole(String providerName, AuthRole role) {
        User user = userRepository.findById(providerName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with provider: " + providerName));

        userRepository.save(user.toBuilder()
                .role(role)
                .build());

    }
}

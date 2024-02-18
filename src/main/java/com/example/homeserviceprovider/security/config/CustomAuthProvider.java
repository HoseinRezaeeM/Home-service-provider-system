package com.example.homeserviceprovider.security.config;

import com.example.homeserviceprovider.domain.user.Users;

import com.example.homeserviceprovider.service.Impl.CustomerUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthProvider implements AuthenticationProvider {

    private final CustomerUserDetailsService customerUserDetailsService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Users users = (Users) customerUserDetailsService
                .loadUserByUsername(authentication.getName()
                );
        if (passwordEncoder.matches(((String) authentication.getCredentials()), users.getPassword())) {
            return new UsernamePasswordAuthenticationToken(
                    users, null, users.getAuthorities()
            );
        }
        throw new BadCredentialsException("wrong information");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
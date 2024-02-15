package com.example.homeserviceprovider.mapper;

import com.example.homeserviceprovider.domain.user.Admin;
import com.example.homeserviceprovider.dto.request.AdminRegistrationDTO;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static com.example.homeserviceprovider.domain.user.enums.Role.ADMIN;


@Component
@RequiredArgsConstructor
public class AdminMapper {

    private final PasswordEncoder passwordEncoder;

    public Admin convertToNewAdmin(AdminRegistrationDTO dto) {
        return new Admin(
                dto.getFirstname(),
                dto.getLastname(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                ADMIN
        );
    }

}

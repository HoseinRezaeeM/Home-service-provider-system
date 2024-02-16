package com.example.homeserviceprovider.service.Impl;

import com.example.homeserviceprovider.domain.user.Users;
import com.example.homeserviceprovider.domain.user.enums.Role;
import com.example.homeserviceprovider.domain.user.enums.SpecialistStatus;
import com.example.homeserviceprovider.dto.request.AdminRegistrationDTO;
import com.example.homeserviceprovider.dto.request.CustomerRegistrationDTO;
import com.example.homeserviceprovider.dto.request.SpecialistRegistrationDTO;
import com.example.homeserviceprovider.exception.ValidationTokenException;
import com.example.homeserviceprovider.exception.VerifyCodeException;
import com.example.homeserviceprovider.security.token.entity.Token;
import com.example.homeserviceprovider.security.token.service.TokenService;
import com.example.homeserviceprovider.service.*;
import com.example.homeserviceprovider.util.Validation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private static final String ADMIN_VERIFY_CODE = "admin_provider_4454";

    private final CustomerService customerService;
    private final SpecialistService specialistService;
    private final AdminService adminService;
    private final Validation validation;
    private final TokenService tokenService;


    public RegistrationServiceImpl(CustomerService customerService, SpecialistService specialistService,
                                   AdminService adminService, Validation validation,
                                   TokenService tokenService) {
        this.customerService = customerService;
        this.specialistService = specialistService;
        this.adminService = adminService;

        this.validation = validation;
        this.tokenService = tokenService;
    }


    @Override
    public String addAdmin(AdminRegistrationDTO dto) {
        if (!(dto.getVerifyCode().equals(ADMIN_VERIFY_CODE)))
            throw new VerifyCodeException(
                    "verify code invalid! call the ADMIN.");
        validation.checkEmail(dto.getEmail());
        return adminService.addNewAdmin(dto);
    }

    @Override
    public String addCustomer(CustomerRegistrationDTO dto) {
        validation.checkEmail(dto.getEmail());
        return customerService.addNewCustomer(dto);
    }

    @Override
    public String addSpecialist(SpecialistRegistrationDTO dto) throws IOException {
        validation.checkEmail(dto.getEmail());
        return specialistService.addNewSpecialist(dto);
    }

    @Transactional
    @Override
    public String confirmToken(String token) {
        Optional<Token> confirmToken = tokenService.getToken(token);
        if (confirmToken.isEmpty()) {
            throw new ValidationTokenException("Token not found!");
        }
        if (confirmToken.get().getConfirmedAt() != null) {
            throw new ValidationTokenException("Email is already confirmed");
        }
        if ((confirmToken.get().getExpiresAt()).isBefore(LocalDateTime.now())) {
            throw new ValidationTokenException("Token is already expired!");
        }
        Users users = confirmToken.get().getUsers();
        if ((users.getRole()).equals(Role.SPECIALIST))
            specialistService.findById(users.getId()).get().setStatus(SpecialistStatus.AWAITING);
        confirmToken.get().getUsers().setIsActive(true);
        tokenService.setConfirmedAt(token);

        return "\n-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-" +
               "-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-" +
               "-*-*-*-*-*-*-* Your email is confirmed *-*-*-*-*-*-*-" +
               "-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-" +
               "-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-";
    }
}

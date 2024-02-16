package com.example.homeserviceprovider.controller;

import com.example.homeserviceprovider.dto.request.AdminRegistrationDTO;
import com.example.homeserviceprovider.dto.request.CustomerRegistrationDTO;
import com.example.homeserviceprovider.dto.request.SpecialistRegistrationDTO;
import com.example.homeserviceprovider.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping("/signup-admin")
    public String adminSingUp(
            @RequestBody AdminRegistrationDTO adminRegistrationDTO) {
        return registrationService.addAdmin(adminRegistrationDTO);
    }

    @PostMapping("/signup-customer")
    public String customerSingUp(
            @RequestBody CustomerRegistrationDTO customerRegistrationDTO) {
        return registrationService.addCustomer(customerRegistrationDTO);
    }

    @PostMapping("/signup-specialist")
    public String specialistSingUp(
            @ModelAttribute SpecialistRegistrationDTO specialistRegistrationDTO) throws IOException {
        return registrationService.addSpecialist(specialistRegistrationDTO);
    }

    /*
    http://localhost:8080/registration/confirm?token=
    */
    @GetMapping("/confirm")
    public String confirm(@RequestParam("token") String token) {
        return registrationService.confirmToken(token);
    }
}



package com.example.homeserviceprovider.service;

import com.example.homeserviceprovider.dto.request.AdminRegistrationDTO;
import com.example.homeserviceprovider.dto.request.CustomerRegistrationDTO;
import com.example.homeserviceprovider.dto.request.SpecialistRegistrationDTO;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

public interface RegistrationService {

    @Transactional
    String confirmToken(String token);

    String addCustomer(CustomerRegistrationDTO customerRegistrationDTO);

    String addSpecialist(SpecialistRegistrationDTO specialistRegistrationDTO) throws IOException;

    String addAdmin(AdminRegistrationDTO adminRegistrationDTO);

}

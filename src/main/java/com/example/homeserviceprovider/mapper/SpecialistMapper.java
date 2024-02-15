package com.example.homeserviceprovider.mapper;

import com.example.homeserviceprovider.domain.user.Specialist;
import com.example.homeserviceprovider.dto.request.SpecialistRegistrationDTO;
import com.example.homeserviceprovider.dto.response.FilterUserResponseDTO;
import com.example.homeserviceprovider.dto.response.SpecialistResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SpecialistMapper {

    private final PasswordEncoder passwordEncoder;

    public SpecialistResponseDTO convertToDTO(Specialist specialist) {
        return new SpecialistResponseDTO(
                specialist.getId(),
                specialist.getFirstname(),
                specialist.getLastname(),
                specialist.getEmail(),
                specialist.getIsActive(),
                specialist.getStatus(),
                specialist.getScore(),
                specialist.getCredit(),
                specialist.getRegistrationTime()
        );
    }

    public Specialist convertToNewSpecialist(SpecialistRegistrationDTO specialistRegistrationDTO) throws IOException {
        return new Specialist(
                specialistRegistrationDTO.getFirstname(),
                specialistRegistrationDTO.getLastname(),
                specialistRegistrationDTO.getEmail(),
                passwordEncoder.encode(specialistRegistrationDTO.getPassword()),
                specialistRegistrationDTO.getProvince(),
                specialistRegistrationDTO.getFile().getBytes()
        );
    }

    public FilterUserResponseDTO convertToFilterDTO(Specialist specialist) {
        return new FilterUserResponseDTO(
                specialist.getRegistrationTime(),
                specialist.getRole().name(),
                specialist.getStatus().name(),
                specialist.getIsActive(),
                specialist.getId(),
                specialist.getFirstname(),
                specialist.getLastname(),
                specialist.getEmail(),
                specialist.getEmail(),
                specialist.getCredit(),
                specialist.getScore()

        );
    }
}

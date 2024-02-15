package com.example.homeserviceprovider.mapper;

import com.example.homeserviceprovider.domain.service.MainServices;
import com.example.homeserviceprovider.dto.request.MainServiceRequestDTO;
import com.example.homeserviceprovider.dto.response.MainServiceResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class MainServiceMapper {

    public MainServiceResponseDTO convertToDTO(MainServices mainService) {
        return new MainServiceResponseDTO(
                mainService.getName()
        );
    }

    public MainServices convertToMainService(MainServiceRequestDTO dto) {
        return new MainServices(
                dto.getName()
        );
    }

}

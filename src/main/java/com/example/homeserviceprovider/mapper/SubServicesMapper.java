package com.example.homeserviceprovider.mapper;

import com.example.homeserviceprovider.domain.service.MainServices;
import com.example.homeserviceprovider.domain.service.SubServices;
import com.example.homeserviceprovider.dto.request.SubServicesRequestDTO;
import com.example.homeserviceprovider.dto.response.SubServicesResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubServicesMapper {

      public SubServicesResponseDTO convertToDTO(SubServices subServices) {
        return new SubServicesResponseDTO(
                subServices.getMainServices().getName(),
                subServices.getId(),
                subServices.getName(),
                subServices.getDescription(),
                subServices.getBasePrice()
        );
    }

    public SubServices convertToJob(SubServicesRequestDTO jrDTO) {
        return new SubServices(
                jrDTO.getName(),
                jrDTO.getBasePrice(),
                jrDTO.getDescription(),
                new MainServices(jrDTO.getMainServiceRequest())
        );
    }

}

package com.example.homeserviceprovider.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class SubServicesResponseDTO {

    String mainServiceName;
    Long subServicesId;
    String subServicesName;
    String description;
    Long basePrice;
}

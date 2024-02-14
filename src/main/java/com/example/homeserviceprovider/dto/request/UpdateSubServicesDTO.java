package com.example.homeserviceprovider.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSubServicesDTO {

    private Long subServicesId;
    private Long mainServiceRequestID;
    private String name;
    private String description;
    private Long basePrice;

}

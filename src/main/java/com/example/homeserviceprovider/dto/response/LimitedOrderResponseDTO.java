package com.example.homeserviceprovider.dto.response;

import com.example.homeserviceprovider.domain.order.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class LimitedOrderResponseDTO {

    String limitedAddress;
    String description;
    Long orderProposedPrice;
    OrderStatus orderStatus;
    String subServicesName;
    String mainServiceName;
    @JsonFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss")
    LocalDateTime orderCreationDate;
    @JsonFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss")
    LocalDateTime orderEndDate;
    String durationOfWork;

}

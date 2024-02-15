package com.example.homeserviceprovider.dto.response;

import com.example.homeserviceprovider.domain.address.Address;
import com.example.homeserviceprovider.domain.offer.Offer;
import com.example.homeserviceprovider.domain.order.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class OrderResponseDTO {

    Address address;
    String description;
    Long orderProposedPrice;
    OrderStatus orderStatus;
    List<Offer> offerList = new ArrayList<>();
    String subServicesName;
    String mainServiceName;
    @JsonFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss")
    LocalDateTime orderCreationDate;
    @JsonFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss")
    LocalDateTime orderEndDate;
    String durationOfWork;

}

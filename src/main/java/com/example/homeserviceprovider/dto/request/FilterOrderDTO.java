package com.example.homeserviceprovider.dto.request;

import com.example.homeserviceprovider.domain.order.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilterOrderDTO {

    Long mainServiceId;
    Long subServiceId;
    String description;
    OrderStatus orderStatus;

    Long minProposedPrice;
    Long maxProposedPrice;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime minOrderRegistrationDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime maxOrderRegistrationDate;

}

package com.example.homeserviceprovider.dto.response;


import com.example.homeserviceprovider.domain.offer.enums.OfferStatus;
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
public class OfferResponseDTO {

    Long offerId;
    Long specialistId;
    String specialistUsername;
    Long orderId;
    Long offerProposedPrice;
    OfferStatus offerStatus;
    @JsonFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss")
    LocalDateTime proposedStartDate;
    @JsonFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss")
    LocalDateTime proposedEndDate;
    String durationOfWork;
}

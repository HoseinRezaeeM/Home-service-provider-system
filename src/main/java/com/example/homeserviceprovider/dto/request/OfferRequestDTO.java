package com.example.homeserviceprovider.dto.request;

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
public class OfferRequestDTO {

    Long orderId;
    Long offerProposedPrice;
    @JsonFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss")
    LocalDateTime proposedStartDate;
    @JsonFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss")
    LocalDateTime proposedEndDate;

}

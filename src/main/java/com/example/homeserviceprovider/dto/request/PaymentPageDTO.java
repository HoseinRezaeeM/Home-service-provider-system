package com.example.homeserviceprovider.dto.request;

import com.example.homeserviceprovider.domain.user.Customer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentPageDTO {
    Long price;
    String captcha;
    String hidden;
    String image;
    CustomerIdOrderIdDTO customerIdOrderIdDTO;

}

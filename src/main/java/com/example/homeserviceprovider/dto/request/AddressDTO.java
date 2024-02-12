package com.example.homeserviceprovider.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class AddressDTO {

    @NonNull
    String province;
    @NonNull
    String city;
    @NonNull
    String avenue;
    @NonNull
    String postalCode;
    String houseNumber;
    String moreDescription;

}

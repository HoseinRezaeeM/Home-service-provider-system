package com.example.homeserviceprovider.mapper;

import com.example.homeserviceprovider.domain.address.Address;
import com.example.homeserviceprovider.dto.request.AddressDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FilterMapper {

    public Address convertToAddress(AddressDTO dto) {
        return new Address(
                dto.getProvince(),
                dto.getCity(),
                dto.getAvenue(),
                dto.getPostalCode(),
                dto.getHouseNumber(),
                dto.getMoreDescription()
        );
    }

    public AddressDTO convertToDTO(Address address) {
        return new AddressDTO(
                address.getProvince(),
                address.getCity(),
                address.getAvenue(),
                address.getPostalCode(),
                address.getHouseNumber(),
                address.getMoreDescription()
        );
    }

}

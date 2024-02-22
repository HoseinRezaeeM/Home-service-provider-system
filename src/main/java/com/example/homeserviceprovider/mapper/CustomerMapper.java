package com.example.homeserviceprovider.mapper;

import com.example.homeserviceprovider.domain.user.Customer;
import com.example.homeserviceprovider.domain.user.enums.CustomerStatus;
import com.example.homeserviceprovider.domain.user.enums.Role;
import com.example.homeserviceprovider.dto.request.CustomerRegistrationDTO;
import com.example.homeserviceprovider.dto.response.FilterUserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerMapper {

      private final PasswordEncoder passwordEncoder;

      public Customer convertToNewClient(CustomerRegistrationDTO dto) {
            Customer customer = new Customer();
            customer.setFirstname(dto.getFirstname());
            customer.setLastname(dto.getLastname());
            customer.setEmail(dto.getEmail());
            customer.setPassword(passwordEncoder.encode(dto.getPassword()));
            customer.setRole(Role.CUSTOMER);
            customer.setCredit(0L);
            customer.setIsActive(false);
            customer.setPaidCounter(0);
            customer.setCustomerStatus(CustomerStatus.HAS_NOT_ORDER_YET);
            return customer;
      }

      public FilterUserResponseDTO convertToFilterDTO(Customer customer) {
            return new FilterUserResponseDTO(
                   customer.getRegistrationTime(),
                   customer.getRole().name(),
                   customer.getCustomerStatus().name(),
                   customer.getIsActive(),
                   customer.getId(),
                   customer.getFirstname(),
                   customer.getLastname(),
                   customer.getEmail(),
                   customer.getEmail(),
                   customer.getCredit(),
                   -1L,
                   customer.getNumberOfOperation(),
                   customer.getPaidCounter()
            );
      }
}

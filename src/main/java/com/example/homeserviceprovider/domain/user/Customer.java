package com.example.homeserviceprovider.domain.user;


import com.example.homeserviceprovider.domain.address.Address;
import com.example.homeserviceprovider.domain.order.Order;
import com.example.homeserviceprovider.domain.user.enums.CustomerStatus;
import com.example.homeserviceprovider.domain.user.enums.Role;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Customer extends Users {
      Long credit;
      @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL)
      @ToString.Exclude
      List<Address> addressList ;
      @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
      @ToString.Exclude
      List<Order> orderList ;
      @Enumerated(value = EnumType.STRING)
      CustomerStatus customerStatus;

      int paidCounter;

      int numberOfOperation;

      public Customer(String firstname, String lastname, String email,
                      String password, Role role, List<Address> addressList, List<Order> orderList) {
            super(firstname, lastname, email, password, Role.CUSTOMER);
            this.credit = 0L;
            this.paidCounter = 0;
            this.numberOfOperation = 0;
            this.addressList = addressList;
            this.orderList = orderList;
      }

      public Customer(String firstname, String lastname, String email, String password) {
            super(firstname, lastname, email, password, Role.CUSTOMER);
            this.credit = 0L;
            this.customerStatus=CustomerStatus.NEW;
            this.paidCounter = 0;
            this.numberOfOperation = 0;
      }


}

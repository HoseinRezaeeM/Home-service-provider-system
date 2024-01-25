package com.example.homeserviceprovider.domain.user;


import com.example.homeserviceprovider.domain.address.Address;
import com.example.homeserviceprovider.domain.order.Order;
import com.example.homeserviceprovider.domain.user.enums.Role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
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
      @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL)
      @ToString.Exclude
      List<Order> orderList ;

      public Customer(String firstname, String lastname, String email,
                      String password, Role role, List<Address> addressList, List<Order> orderList) {
            super(firstname, lastname, email, password, Role.CUSTOMER);
            this.credit = 0L;
            this.addressList = addressList;
            this.orderList = orderList;
      }

      public Customer(String firstname, String lastname, String email, String password) {
            super(firstname, lastname, email, password, Role.CUSTOMER);
            this.credit = 0L;
      }

      public Customer(long id) {
            super(id);
      }
}

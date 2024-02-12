package com.example.homeserviceprovider.domain.service;

import com.example.homeserviceprovider.base.domain.BaseEntity;
import com.example.homeserviceprovider.domain.order.Order;
import com.example.homeserviceprovider.domain.user.Specialist;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.util.List;


@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubServices extends BaseEntity<Long> {
      @Column(unique = true)
      String name;
      Long basePrice;
      String description;
      @ManyToOne(cascade = CascadeType.ALL)
      MainServices mainServices;
      @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
      List<Specialist> specialistList ;
      @OneToMany(mappedBy = "subServices",cascade = CascadeType.ALL)
      List<Order> orderList;


      public SubServices(String name, Long basePrice, String description, MainServices mainServices) {
            this.name = name;
            this.basePrice = basePrice;
            this.description = description;
            this.mainServices = mainServices;
      }


      public SubServices(String name, Long basePrice, String description) {
            this.name = name;
            this.basePrice = basePrice;
            this.description = description;
      }

}

package com.example.homeserviceprovider.domain.order;


import com.example.homeserviceprovider.base.domain.BaseEntity;
import com.example.homeserviceprovider.domain.address.Address;
import com.example.homeserviceprovider.domain.comment.Comment;
import com.example.homeserviceprovider.domain.offer.Offer;
import com.example.homeserviceprovider.domain.order.enums.OrderStatus;
import com.example.homeserviceprovider.domain.service.SubServices;
import com.example.homeserviceprovider.domain.user.Customer;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.time.LocalDateTime;
import java.util.List;

import static com.example.homeserviceprovider.domain.order.enums.OrderStatus.WAITING_FOR_SPECIALIST_SUGGESTION;

import static jakarta.persistence.EnumType.STRING;


@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Order_Table")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order extends BaseEntity<Long> {

      LocalDateTime executionTime;
      LocalDateTime endTime;
      Long proposedPrice;
      String description;
      @Enumerated(value = STRING)
      OrderStatus orderStatus;
      @ManyToOne(cascade = CascadeType.MERGE)
      Customer customer;
      @ManyToOne(cascade = CascadeType.MERGE)
      SubServices subServices;
      @OneToMany(mappedBy = "order",fetch = FetchType.EAGER)
      List<Offer> offerList;
      @OneToOne
      Comment comment;
      @OneToOne
      Address address;



      public Order(LocalDateTime executionTime, LocalDateTime endTime, Long proposedPrice,
                   String description, Customer customer, SubServices subServices,Address address) {
            this.executionTime = executionTime;
            this.endTime = endTime;
            this.proposedPrice = proposedPrice;
            this.description = description;
            this.orderStatus = WAITING_FOR_SPECIALIST_SUGGESTION;
            this.customer = customer;
            this.subServices = subServices;
            this.address=address;
      }

      public Order(LocalDateTime executionTime, LocalDateTime endTime,
                   Long proposedPrice, String description, SubServices subServices, Address address) {
            this.executionTime = executionTime;
            this.endTime = endTime;
            this.proposedPrice = proposedPrice;
            this.description = description;
            this.subServices = subServices;
            this.address = address;
            this.orderStatus = WAITING_FOR_SPECIALIST_SUGGESTION;
      }

      public Order(long id) {
            super(id);
      }
}

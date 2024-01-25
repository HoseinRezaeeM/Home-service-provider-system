package com.example.homeserviceprovider.domain.offer;


import com.example.homeserviceprovider.base.domain.BaseEntity;
import com.example.homeserviceprovider.domain.offer.enums.OfferStatus;
import com.example.homeserviceprovider.domain.order.Order;
import com.example.homeserviceprovider.domain.user.Specialist;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Offer extends BaseEntity<Long> {


      LocalDateTime executionTime;
      LocalDateTime endTime;
      Long proposedPrice;
      @Enumerated(value = EnumType.STRING)
      OfferStatus offerStatus;
      @ManyToOne(fetch = FetchType.EAGER)
      Order order;
      @ManyToOne
      Specialist specialist;

      public Offer(LocalDateTime executionTime, LocalDateTime endTime, Long proposedPrice) {
            this.executionTime = executionTime;
            this.endTime = endTime;
            this.proposedPrice = proposedPrice;
            this.offerStatus = OfferStatus.WAITING;
      }
}

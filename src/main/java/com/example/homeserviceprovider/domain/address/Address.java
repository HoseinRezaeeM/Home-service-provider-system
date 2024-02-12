package com.example.homeserviceprovider.domain.address;


import com.example.homeserviceprovider.base.domain.BaseEntity;
import com.example.homeserviceprovider.domain.user.Customer;
import io.micrometer.common.lang.NonNull;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Address extends BaseEntity<Long> {
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
      @ManyToOne(fetch = FetchType.EAGER)
      Customer customer;

      public Address(@NonNull String province, @NonNull String city, @NonNull String avenue,
                     @NonNull String postalCode, String houseNumber, String moreDescription) {
            this.province = province;
            this.city = city;
            this.avenue = avenue;
            this.postalCode = postalCode;
            this.houseNumber = houseNumber;
            this.moreDescription = moreDescription;
      }
}

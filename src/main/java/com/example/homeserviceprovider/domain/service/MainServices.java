package com.example.homeserviceprovider.domain.service;

import com.example.homeserviceprovider.base.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MainServices extends BaseEntity<Long> {

      @Column(nullable = false)
      String name;
      @OneToMany(mappedBy = "mainServices",cascade = CascadeType.ALL)
      List<SubServices> subServicesList ;

      public MainServices(String name) {
            this.name = name;
      }


}

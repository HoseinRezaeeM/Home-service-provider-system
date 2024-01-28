package com.example.homeserviceprovider.base.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.io.Serializable;


@Getter
@Setter
@ToString
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@FieldDefaults(level = AccessLevel.PRIVATE)
public  class BaseEntity<ID extends Serializable> {
      @Id @GeneratedValue(strategy = GenerationType.AUTO)
      ID id;


}

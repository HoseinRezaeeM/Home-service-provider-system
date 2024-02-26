package com.example.homeserviceprovider.base.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
@ToString
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public  class BaseEntity<ID extends Serializable> {
      @Id @GeneratedValue(strategy = GenerationType.AUTO)
      ID id;
      @JsonFormat(pattern = "yyyy-MM-dd")
      LocalDateTime registrationTime = LocalDateTime.now();

}

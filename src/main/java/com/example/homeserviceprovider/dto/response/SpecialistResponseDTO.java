package com.example.homeserviceprovider.dto.response;

import com.example.homeserviceprovider.domain.user.enums.SpecialistStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class SpecialistResponseDTO {

      Long specialistId;
      String firstname;
      String lastname;
      String email;
      Boolean isActive;
      SpecialistStatus specialistStatus;
      double score;
      Long credit;
      int numberOfOperation;
      int numberOfDoneOperation;
      @JsonFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss")
      LocalDateTime registrationTime;

}

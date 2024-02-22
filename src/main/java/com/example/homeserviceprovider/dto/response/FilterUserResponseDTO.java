package com.example.homeserviceprovider.dto.response;

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
public class FilterUserResponseDTO {

      @JsonFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss")
      LocalDateTime userCreationDate;
      String userType;
      String userStatus;
      Boolean isActive;
      Long userId;
      String firstname;
      String lastname;
      String email;
      String username;
      Long credit;
      double score_JustForSpecialist;
      int numberOfOperation;
      int numberOfDoneOperation;


}

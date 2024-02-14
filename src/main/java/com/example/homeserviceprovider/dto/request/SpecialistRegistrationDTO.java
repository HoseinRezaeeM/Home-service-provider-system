package com.example.homeserviceprovider.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class SpecialistRegistrationDTO {

    String firstname;
    String lastname;
    String email;
    String password;
    String province;
    MultipartFile file;

}

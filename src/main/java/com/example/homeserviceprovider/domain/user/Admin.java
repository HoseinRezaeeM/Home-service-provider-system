package com.example.homeserviceprovider.domain.user;



import com.example.homeserviceprovider.domain.user.enums.Role;

import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;




@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Admin extends Users {
      public Admin(String firstname, String lastname, String email, String password, Role role) {
            super(firstname, lastname, email, password, Role.ADMIN);
      }
}

package com.example.homeserviceprovider.domain.user;

import com.example.homeserviceprovider.base.domain.BaseEntity;
import com.example.homeserviceprovider.domain.user.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;



@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Users extends BaseEntity<Long> {

      String firstname;
      String lastname;
      @Column(unique = true)
      String email;
      String password;
      @Enumerated(value = EnumType.STRING)
      Role role;

      public Users(String firstname, String lastname, String email, String password, Role role) {
            this.firstname = firstname;
            this.lastname = lastname;
            this.email = email;
            this.password = password;
            this.role = role;
      }

      public Users(long id) {
            super(id);
      }
}

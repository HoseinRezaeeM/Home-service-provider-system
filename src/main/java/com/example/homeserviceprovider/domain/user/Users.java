package com.example.homeserviceprovider.domain.user;

import com.example.homeserviceprovider.base.domain.BaseEntity;
import com.example.homeserviceprovider.domain.user.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Users extends BaseEntity<Long> implements UserDetails {

      String firstname;
      String lastname;
      @Column(unique = true)
      String email;
      String password;
      @Enumerated(value = EnumType.STRING)
      Role role;
      Boolean isActive;

      public Users(String firstname, String lastname, String email, String password, Role role) {
            this.firstname = firstname;
            this.lastname = lastname;
            this.email = email;
            this.password = password;
            this.role = role;
      }


      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(role.name());
            return Collections.singleton(simpleGrantedAuthority);
      }

      @Override
      public String getUsername() {
            return email;
      }

      @Override
      public String getPassword() {
            return password;}

      @Override
      public boolean isAccountNonExpired() {
            return true;
      }

      @Override
      public boolean isAccountNonLocked() {
            return false;
      }

      @Override
      public boolean isCredentialsNonExpired() {
            return true;
      }

      @Override
      public boolean isEnabled() {
            return isActive;
      }
}

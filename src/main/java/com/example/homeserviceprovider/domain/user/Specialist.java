package com.example.homeserviceprovider.domain.user;



import com.example.homeserviceprovider.domain.offer.Offer;
import com.example.homeserviceprovider.domain.service.SubServices;
import com.example.homeserviceprovider.domain.user.enums.Role;
import com.example.homeserviceprovider.domain.user.enums.SpecialistStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Specialist extends Users {
      @Lob
      byte[] image;
      double score;
      Long credit;

      @Enumerated(value = EnumType.STRING)
      SpecialistStatus status;
      @ManyToMany(mappedBy = "specialistList", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
      List<SubServices> subServicesList;
      @OneToMany(mappedBy = "specialist")
      @ToString.Exclude
      List<Offer> offerList;
      String province;
      int paidCounter;
      int numberOfOperation;
      int rateCounter;
      public Specialist(String firstname, String lastname, String email, String password, String province,
                        byte[] image) {
            super(firstname, lastname, email, password, Role.SPECIALIST);
            this.score = 0;
            this.status = SpecialistStatus.NEW;
            this.image = image;
            this.credit = 0L;
            this.province = province;
            this.rateCounter = 0;
      }



      public void addSubServices(SubServices subServices) {
            this.subServicesList.add(subServices);
            subServices.getSpecialistList().add(this);
      }

      public void deleteSubServices(SubServices subServices) {
            this.subServicesList.remove(subServices);
            subServices.getSpecialistList().remove(this);
      }

      public void delay(int hours) {
            this.score = this.score - hours;
            checkRate();
      }

      private void checkRate() {
            if (this.score < 0) {
                  this.setIsActive(false);
                  this.status = SpecialistStatus.AWAITING;
                  this.score = 0;
            }
      }
}
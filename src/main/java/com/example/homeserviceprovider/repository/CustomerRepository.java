package com.example.homeserviceprovider.repository;


import com.example.homeserviceprovider.base.repository.BaseEntityRepository;
import com.example.homeserviceprovider.domain.user.Customer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends BaseEntityRepository<Customer, Long> {


      Optional<Customer> findByEmail(String email);

      @Modifying
      @Query(" update Customer c set c.password = :newPassword where c.email = :email")
      void editPassword(String email, String newPassword);

      @Modifying
      @Query("update Customer c set c.credit = :newCredit where c.id = :customerId")
      void updateCredit(Long customerId, Long newCredit);

}

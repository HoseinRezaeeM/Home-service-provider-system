package com.example.homeserviceprovider.repository;

import com.example.homeserviceprovider.base.repository.BaseEntityRepository;
import com.example.homeserviceprovider.domain.user.Specialist;
import com.example.homeserviceprovider.domain.user.enums.SpecialistStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialistRepository extends BaseEntityRepository<Specialist, Long> {

    boolean existsByEmail(String email);

    Optional<Specialist> findByEmail(String email);

    @Modifying
    @Query("update Specialist s set s.password = :newPassword where s.id = :SpecialistId")
    void editPassword(Long SpecialistId, String newPassword);


    List<Specialist> findSpecialistByStatus(SpecialistStatus specialistStatus);

   @Modifying
   @Query("update Specialist s set s.credit = :credit where s.id = :id")
   void updateCredit(Long credit,Long id);



}

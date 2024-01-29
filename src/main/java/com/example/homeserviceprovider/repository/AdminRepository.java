package com.example.homeserviceprovider.repository;


import com.example.homeserviceprovider.base.repository.BaseEntityRepository;
import com.example.homeserviceprovider.domain.user.Admin;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends BaseEntityRepository<Admin, Long> {


    Optional<Admin> findByEmail(String email);



}

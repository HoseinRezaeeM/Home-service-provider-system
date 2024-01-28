package com.example.homeserviceprovider.repository;

import com.example.homeserviceprovider.base.repository.BaseEntityRepository;
import com.example.homeserviceprovider.domain.service.SubServices;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubServicesRepository extends BaseEntityRepository<SubServices, Long> {

    Optional<SubServices> findByName(String jobName);

    void deleteByName(String jobName);

    @Query("select s from SubServices s where s.mainServices.id = :mainServiceId")
    List<SubServices> findByMainServiceId(Long mainServiceId);

    @Query("select s from SubServices s where s.mainServices.name = :mainServiceName")
    List<SubServices> findByMainServiceName(String mainServiceName);
}

package com.example.homeserviceprovider.repository;

import com.example.homeserviceprovider.base.repository.BaseEntityRepository;
import com.example.homeserviceprovider.domain.service.MainServices;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MainServiceRepository extends BaseEntityRepository<MainServices, Long> {

    Optional<MainServices> findByName(String mainServiceName);

    void deleteByName(String mainServiceName);

}

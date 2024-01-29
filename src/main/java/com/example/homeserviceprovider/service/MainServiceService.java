package com.example.homeserviceprovider.service;

import com.example.homeserviceprovider.base.service.BaseEntityService;
import com.example.homeserviceprovider.domain.service.MainServices;


import java.util.List;
import java.util.Optional;

public interface MainServiceService extends BaseEntityService<MainServices,Long> {
    @Override
    void save(MainServices mainService);

    @Override
    void delete(MainServices mainService);

    @Override
    Optional<MainServices> findById(Long aLong);

    @Override
    List<MainServices> findAll();

    Optional<MainServices> findByName(String mainServiceName);

    void deleteMainServiceByName(String mainServiceName);

}

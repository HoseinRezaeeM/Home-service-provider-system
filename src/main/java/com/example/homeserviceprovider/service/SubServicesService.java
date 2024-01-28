package com.example.homeserviceprovider.service;

import com.example.homeserviceprovider.base.service.BaseEntityService;
import com.example.homeserviceprovider.domain.service.SubServices;


import java.util.List;
import java.util.Optional;

public interface SubServicesService extends BaseEntityService<SubServices,Long> {

    @Override
    void save(SubServices subServices);

    @Override
    void delete(SubServices subServices);

    @Override
    Optional<SubServices> findById(Long aLong);

    @Override
    List<SubServices> findAll();

    Optional<SubServices> findByName(String subServicesName);

    void deleteSubServicesByName(String subServicesName);

    List<SubServices> findByMainServiceName(String mainServiceName);

    List<SubServices> findByMainServiceId(Long mainServiceId);
}

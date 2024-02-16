package com.example.homeserviceprovider.service;

import com.example.homeserviceprovider.base.service.BaseEntityService;
import com.example.homeserviceprovider.domain.service.SubServices;
import com.example.homeserviceprovider.dto.request.SubServicesRequestDTO;
import com.example.homeserviceprovider.dto.response.SubServicesResponseDTO;


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

    List<SubServicesResponseDTO> findByMainServiceName(String mainServiceName);

    List<SubServicesResponseDTO> findByMainServiceId(Long mainServiceId);
}

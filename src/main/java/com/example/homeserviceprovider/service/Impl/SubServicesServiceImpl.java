package com.example.homeserviceprovider.service.Impl;

import com.example.homeserviceprovider.base.service.impl.BaseEntityServiceImpl;
import com.example.homeserviceprovider.domain.service.SubServices;
import com.example.homeserviceprovider.exception.MainServicesIsNotExistException;
import com.example.homeserviceprovider.repository.SubServicesRepository;
import com.example.homeserviceprovider.service.SubServicesService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SubServicesServiceImpl extends BaseEntityServiceImpl<SubServices, Long, SubServicesRepository>
        implements SubServicesService {



    public SubServicesServiceImpl(SubServicesRepository repository) {
        super(repository);

    }

    @Override
    public Optional<SubServices> findByName(String subServicesName) {
        return repository.findByName(subServicesName);
    }

    @Override
    public List<SubServices> findAll() {
        List<SubServices> subServicesList = repository.findAll();
        if (subServicesList.isEmpty())
            throw new MainServicesIsNotExistException("there are no jobs!");
        return subServicesList;
    }

    @Override
    public void deleteSubServicesByName(String subServicesName) {
        repository.deleteByName(subServicesName);
    }

    @Override
    public List<SubServices> findByMainServiceName(String mainServiceName) {
        List<SubServices> subServicesList = repository.findByMainServiceName(mainServiceName);
        return subServicesList.stream().toList() ;
    }
    @Override
    public List<SubServices> findByMainServiceId(Long mainServiceId) {
        List<SubServices> subServicesList = repository.findByMainServiceId(mainServiceId);
        return subServicesList.stream().toList();
    }

}

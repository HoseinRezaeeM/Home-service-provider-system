package com.example.homeserviceprovider.service.Impl;

import com.example.homeserviceprovider.base.service.impl.BaseEntityServiceImpl;
import com.example.homeserviceprovider.domain.service.MainServices;
import com.example.homeserviceprovider.exception.MainServicesIsNotExistException;
import com.example.homeserviceprovider.repository.MainServiceRepository;
import com.example.homeserviceprovider.service.MainServiceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MainServiceServiceImpl extends BaseEntityServiceImpl<MainServices, Long, MainServiceRepository>
        implements MainServiceService {

    public MainServiceServiceImpl(MainServiceRepository repository) {
        super(repository);
    }

    @Override
    public List<MainServices> findAll() {
        List<MainServices> mainServiceList = repository.findAll();
        if (mainServiceList.isEmpty())
            throw new MainServicesIsNotExistException("there are no main services!");
        return mainServiceList;
    }

    @Override
    public Optional<MainServices> findByName(String mainServiceName) {
        return repository.findByName(mainServiceName);
    }

    @Override
    public void deleteMainServiceByName(String mainServiceName) {
        repository.deleteByName(mainServiceName);
    }
}

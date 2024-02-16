package com.example.homeserviceprovider.service.Impl;

import com.example.homeserviceprovider.base.service.impl.BaseEntityServiceImpl;
import com.example.homeserviceprovider.domain.service.SubServices;
import com.example.homeserviceprovider.dto.response.SubServicesResponseDTO;
import com.example.homeserviceprovider.exception.MainServicesIsNotExistException;
import com.example.homeserviceprovider.mapper.SubServicesMapper;
import com.example.homeserviceprovider.repository.SubServicesRepository;
import com.example.homeserviceprovider.service.SubServicesService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SubServicesServiceImpl extends BaseEntityServiceImpl<SubServices, Long, SubServicesRepository>
       implements SubServicesService {

      private final SubServicesMapper subServicesMapper;

      public SubServicesServiceImpl(SubServicesRepository repository, SubServicesMapper subServicesMapper) {
            super(repository);
            this.subServicesMapper = subServicesMapper;
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
      public List<SubServicesResponseDTO> findByMainServiceName(String mainServiceName) {
            List<SubServices> jobList = repository.findByMainServiceName(mainServiceName);
            List<SubServicesResponseDTO> jrDTOS = new ArrayList<>();
            if (jobList.isEmpty())
                  return jrDTOS;
            jobList.forEach(j -> jrDTOS.add(subServicesMapper.convertToDTO(j)));
            return jrDTOS;
      }

      @Override
      public List<SubServicesResponseDTO> findByMainServiceId(Long mainServiceId) {
            List<SubServices> jobList = repository.findByMainServiceId(mainServiceId);
            List<SubServicesResponseDTO> jrDTOS = new ArrayList<>();
            if (jobList.isEmpty())
                  return jrDTOS;
            jobList.forEach(j -> jrDTOS.add(subServicesMapper.convertToDTO(j)));
            return jrDTOS;
      }

}

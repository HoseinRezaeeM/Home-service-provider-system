package com.example.homeserviceprovider.service.Impl;

import com.example.homeserviceprovider.base.service.impl.BaseEntityServiceImpl;
import com.example.homeserviceprovider.domain.service.MainServices;
import com.example.homeserviceprovider.domain.service.SubServices;
import com.example.homeserviceprovider.domain.user.Admin;
import com.example.homeserviceprovider.domain.user.Specialist;
import com.example.homeserviceprovider.domain.user.enums.SpecialistStatus;
import com.example.homeserviceprovider.exception.*;
import com.example.homeserviceprovider.repository.AdminRepository;
import com.example.homeserviceprovider.service.*;


import com.example.homeserviceprovider.util.Validation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class AdminServiceImpl extends BaseEntityServiceImpl<Admin, Long, AdminRepository>
       implements AdminService {
      private final MainServiceService mainServiceService;
      private final SubServicesService subServicesService;
      private final SpecialistService specialistService;


      private final Validation validation;

      public AdminServiceImpl(AdminRepository repository, MainServiceService mainServiceService, SubServicesService subServicesService,
                              SpecialistService specialistService,
                              Validation validation) {
            super(repository);
            this.mainServiceService = mainServiceService;
            this.subServicesService = subServicesService;
            this.specialistService = specialistService;
            this.validation = validation;
      }

      @Override
      public void addNewAdmin(Admin admin) {
            validation.checkEmail(admin.getEmail());
            if (repository.findByEmail(admin.getEmail()).isPresent())
                  throw new DuplicateEmailException("this Email already exist!");
            repository.save(admin);

      }

      @Override
      public void createMainService(MainServices mainServices) {
            validation.checkText(mainServices.getName());
            if (mainServiceService.findByName(mainServices.getName()).isPresent())
                  throw new MainServicesIsExistException("this main service already exist!");
            MainServices newMainService = new MainServices(mainServices.getName());
            mainServiceService.save(newMainService);
      }

      @Override
      public void deleteMainService(String name) {
            validation.checkText(name);
            if (mainServiceService.findByName(name).isEmpty())
                  throw new MainServicesIsNotExistException("this main service dose not exist!");
            mainServiceService.deleteMainServiceByName(name);
      }

      @Override
      public void addSubServices(SubServices subServices) {
            String mainServiceName = subServices.getMainServices().getName();
            if(mainServiceName.isBlank()){
                  throw new MainServicesIsNotExistException("this main service dose not exist!");
            }
            String subServicesName = subServices.getName();
            validation.checkText(mainServiceName);
            validation.checkText(subServicesName);
            validation.checkPositiveNumber(subServices.getBasePrice());
            validation.checkBlank(subServices.getDescription());
            Optional<MainServices> mainService = mainServiceService.findByName(mainServiceName);
            if (mainService.isEmpty())
                  throw new MainServicesIsNotExistException("this main service dose not exist!");
            if (subServicesService.findByName(subServicesName).isPresent())
                  throw new SubServicesIsExistException("this subServices already exist!");
            subServices.setMainServices(mainService.get());
            subServicesService.save(subServices);

      }

      @Override
      public void deleteSubServices(String name) {
            validation.checkText(name);
            if (subServicesService.findByName(name).isEmpty())
                  throw new SubServicesIsNotExistException("this subServices dose not exist!");
            subServicesService.deleteSubServicesByName(name);
      }

      @Override
      public void addSpecialistToSubServices(Long subServicesId, Long specialistId) {
            validation.checkPositiveNumber(subServicesId);
            validation.checkPositiveNumber(specialistId);
            Optional<SubServices> subServices = subServicesService.findById(subServicesId);
            if (subServices.isEmpty())
                  throw new SubServicesIsNotExistException("this SubServices dose not exist!");
            Optional<Specialist> worker = specialistService.findById(specialistId);
            if (worker.isEmpty())
                  throw new SpecialistIsNotExistException("this specialist does not exist!");
            if (!(worker.get().getStatus().equals(SpecialistStatus.CONFIRMED)))
                  throw new SpecialistNoAccessException("the status of expert is not CONFIRMED");
            worker.get().addSubServices(subServices.get());
            specialistService.save(worker.get());

      }

      @Override
      public void deleteSubServicesFromSpecialist(Long subServicesId, Long specialistId) {
            validation.checkPositiveNumber(subServicesId);
            validation.checkPositiveNumber(specialistId);
            Optional<SubServices> optionalSubServices = subServicesService.findById(subServicesId);
            if (optionalSubServices.isEmpty())
                  throw new SubServicesIsNotExistException("this subServices dose not exist!");
            Optional<Specialist> specialist = specialistService.findById(specialistId);
            if (specialist.isEmpty())
                  throw new SpecialistIsNotExistException("this specialist does not exist!");
            specialist.get().deleteSubServices(optionalSubServices.get());
            specialistService.save(specialist.get());

      }

      @Override
      @Transactional(readOnly = true)
      public List<MainServices> findAllMainService() {
            List<MainServices> mainServices = mainServiceService.findAll();
            return mainServices.stream().toList();

      }

      @Override
      @Transactional(readOnly = true)
      public List<SubServices> findAllSubServices() {
            List<SubServices> subServicesList = subServicesService.findAll();
            return subServicesList.stream().toList();
      }

      @Override
      public void editSubServicesCustom(SubServices subServices) {
            validation.checkText(subServices.getName());
            Optional<SubServices> subServicesOptional;
            if (subServices.getId() != null) {
                  validation.checkPositiveNumber(subServices.getId());
                  subServicesOptional = subServicesService.findById(subServices.getId());
                  if (subServicesOptional.isEmpty())
                        throw new SubServicesIsNotExistException("this subservices dose not exist!");
                  subServicesOptional.get().setName(subServices.getName());
            } else {
                  subServicesOptional = subServicesService.findByName(subServices.getName());
                  if (subServicesOptional.isEmpty())
                        throw new SubServicesIsNotExistException("this subservices dose not exist!");
            }
            if (subServices.getDescription().isEmpty() &&
                subServices.getBasePrice() == 0L) {
                  throw new SubServicesIsNotExistException("change titles are empty!");
            } else if (!subServices.getDescription().isEmpty()) {
                  validation.checkText(subServices.getDescription());
                  subServicesOptional.get().setDescription(subServices.getDescription());
            }
            if (subServices.getBasePrice() != 0L) {
                  validation.checkPositiveNumber(subServices.getBasePrice());
                  subServicesOptional.get().setBasePrice(subServices.getBasePrice());
            }
            subServicesService.save(subServicesOptional.get());


      }


      @Override
      public void confirmSpecialist(Long specialistId) {
            validation.checkPositiveNumber(specialistId);
            Optional<Specialist> optionalSpecialist = specialistService.findById(specialistId);
            if (optionalSpecialist.isEmpty())
                  throw new SpecialistIsNotExistException("this specialists does not exist!");

            if (optionalSpecialist.get().getStatus().equals(SpecialistStatus.CONFIRMED))
                  throw new SpecialistIsHoldsExistException("this specialists is currently certified!");
            optionalSpecialist.get().setStatus(SpecialistStatus.CONFIRMED);

            specialistService.save(optionalSpecialist.get());

      }

      @Override
      @Transactional(readOnly = true)
      public List<SubServices> findAllSubServicesByMainService(Long mainServiceId) {
            validation.checkPositiveNumber(mainServiceId);
            if (mainServiceService.findById(mainServiceId).isEmpty())
                  throw new MainServicesIsNotExistException("this main service dose not exist!");
            return subServicesService.findByMainServiceId(mainServiceId);
      }


      @Override
      public Optional<Admin> findByUsername(String email) {
            return repository.findByEmail(email);
      }
}

package com.example.homeserviceprovider.service.Impl;

import com.example.homeserviceprovider.base.service.impl.BaseEntityServiceImpl;
import com.example.homeserviceprovider.domain.service.MainServices;
import com.example.homeserviceprovider.domain.service.SubServices;
import com.example.homeserviceprovider.domain.user.Admin;
import com.example.homeserviceprovider.domain.user.Specialist;
import com.example.homeserviceprovider.domain.user.enums.SpecialistStatus;
import com.example.homeserviceprovider.dto.request.*;
import com.example.homeserviceprovider.dto.response.*;
import com.example.homeserviceprovider.exception.*;
import com.example.homeserviceprovider.mapper.*;
import com.example.homeserviceprovider.repository.AdminRepository;
import com.example.homeserviceprovider.security.token.entity.Token;
import com.example.homeserviceprovider.security.token.service.TokenService;
import com.example.homeserviceprovider.service.*;


import com.example.homeserviceprovider.util.Validation;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.homeserviceprovider.domain.user.enums.Role.CUSTOMER;
import static com.example.homeserviceprovider.domain.user.enums.Role.SPECIALIST;


@Service
@Transactional
public class AdminServiceImpl extends BaseEntityServiceImpl<Admin, Long, AdminRepository>
       implements AdminService {
      private final MainServiceService mainServiceService;
      private final SubServicesService subServicesService;
      private final OrderService orderService;
      private final SpecialistService specialistService;
      private final CustomerService customerService;

      private final MainServiceMapper mainServiceMapper;
      private final SubServicesMapper subServicesMapper;
      private final SpecialistMapper specialistMapper;
      private final AdminMapper adminMapper;
      private final FilterMapper filterMapper;

      private final Validation validation;
      private final TokenService tokenService;
      private final EmailService emailService;

      public AdminServiceImpl(AdminRepository repository, MainServiceService mainServiceService,
                              SubServicesService subServicesService, OrderService orderService, SpecialistService specialistService,
                              CustomerService customerService, MainServiceMapper mainServiceMapper,
                              SubServicesMapper subServicesMapper, SpecialistMapper specialistMapper,
                              AdminMapper adminMapper, FilterMapper filterMapper, Validation validation,
                              TokenService tokenService, EmailService emailService) {
            super(repository);
            this.mainServiceService = mainServiceService;
            this.subServicesService = subServicesService;
            this.orderService = orderService;
            this.specialistService = specialistService;
            this.customerService = customerService;
            this.mainServiceMapper = mainServiceMapper;
            this.subServicesMapper = subServicesMapper;
            this.specialistMapper = specialistMapper;
            this.adminMapper = adminMapper;
            this.filterMapper = filterMapper;
            this.validation = validation;
            this.tokenService = tokenService;
            this.emailService = emailService;
      }

      @Override
      public String addNewAdmin(AdminRegistrationDTO dto) {
            validation.checkEmail(dto.getEmail());
            if (repository.findByEmail(dto.getEmail()).isPresent())
                  throw new DuplicateEmailException("this Email already exist!");
            Admin admin = adminMapper.convertToNewAdmin(dto);
            repository.save(admin);
            String newToken = UUID.randomUUID().toString();
            Token token = new Token(LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), admin);
            token.setToken(newToken);
            tokenService.saveToken(token);
            SimpleMailMessage mailMessage =
                   emailService.createEmail(admin.getEmail(), admin.getFirstname(),
                          token.getToken(), admin.getRole());
            emailService.sendEmail(mailMessage);
            return newToken;
      }

      @Override
      public ProjectResponse createMainService(MainServiceRequestDTO msDTO) {
            validation.checkText(msDTO.getName());
            if (mainServiceService.findByName(msDTO.getName()).isPresent())
                  throw new MainServicesIsExistException("this main service already exist!");
            MainServices newMainService = new MainServices(msDTO.getName());
            mainServiceService.save(newMainService);
            return new ProjectResponse("200", "ADDED SUCCESSFUL");
      }

      @Override
      public ProjectResponse deleteMainService(String name) {
            validation.checkText(name);
            if (mainServiceService.findByName(name).isEmpty())
                  throw new MainServicesIsNotExistException("this main service dose not exist!");
            mainServiceService.deleteMainServiceByName(name);
            return new ProjectResponse("200", "DELETED SUCCESSFUL");
      }

      @Override
      public ProjectResponse addSubServices(SubServicesRequestDTO subServicesRequestDTO) {
            String mainServiceName = subServicesRequestDTO.getMainServiceRequest();
            String subServicesName = subServicesRequestDTO.getName();
            validation.checkText(mainServiceName);
            validation.checkText(subServicesName);
            validation.checkPositiveNumber(subServicesRequestDTO.getBasePrice());
            validation.checkBlank(subServicesRequestDTO.getDescription());
            Optional<MainServices> mainService = mainServiceService.findByName(mainServiceName);
            if (mainService.isEmpty())
                  throw new MainServicesIsNotExistException("this main service dose not exist!");
            if (subServicesService.findByName(subServicesName).isPresent())
                  throw new SubServicesIsExistException("this job already exist!");
            SubServices newSubServices = subServicesMapper.convertToJob(subServicesRequestDTO);
            newSubServices.setMainServices(mainService.get());
            subServicesService.save(newSubServices);
            return new ProjectResponse("200", "ADDED SUCCESSFUL");
      }

      @Override
      public ProjectResponse deleteSubServices(String name) {
            validation.checkText(name);
            if (subServicesService.findByName(name).isEmpty())
                  throw new SubServicesIsNotExistException("this job dose not exist!");
            subServicesService.deleteSubServicesByName(name);
            return new ProjectResponse("200", "DELETED SUCCESSFUL");
      }

      @Override
      public ProjectResponse addSpecialistToSubServices(Long jobId, Long workerId) {
            validation.checkPositiveNumber(jobId);
            validation.checkPositiveNumber(workerId);
            Optional<SubServices> job = subServicesService.findById(jobId);
            if (job.isEmpty())
                  throw new SubServicesIsNotExistException("this job dose not exist!");
            Optional<Specialist> specialist = specialistService.findById(workerId);
            if (specialist.isEmpty())
                  throw new SpecialistIsNotExistException("this specialist does not exist!");
            if (!(specialist.get().getStatus().equals(SpecialistStatus.CONFIRMED)))
                  throw new SpecialistNoAccessException("the status of expert is not CONFIRMED");
            specialist.get().addSubServices(job.get());
            specialistService.save(specialist.get());
            return new ProjectResponse("200", "ADDED SUCCESSFUL");
      }

      @Override
      public ProjectResponse deleteSubServicesFromSpecialist(Long jobId, Long workerId) {
            validation.checkPositiveNumber(jobId);
            validation.checkPositiveNumber(workerId);
            Optional<SubServices> service = subServicesService.findById(jobId);
            if (service.isEmpty())
                  throw new SubServicesIsNotExistException("this service dose not exist!");
            Optional<Specialist> specialist = specialistService.findById(workerId);
            if (specialist.isEmpty())
                  throw new SpecialistIsNotExistException("this specialist does not exist!");
            specialist.get().deleteSubServices(service.get());
            specialistService.save(specialist.get());
            return new ProjectResponse("200", "DELETED SUCCESSFUL");
      }

      @Override
      @Transactional(readOnly = true)
      public List<MainServiceResponseDTO> findAllMainService() {
            List<MainServices> mainServices = mainServiceService.findAll();
            List<MainServiceResponseDTO> msDTOS = new ArrayList<>();
            mainServices.forEach(ms -> msDTOS.add(mainServiceMapper.convertToDTO(ms)));
            return msDTOS;
      }

      @Override
      @Transactional(readOnly = true)
      public List<SubServicesResponseDTO> findAllSubServices() {
            List<SubServices> jobs = subServicesService.findAll();
            List<SubServicesResponseDTO> jDTOS = new ArrayList<>();
            jobs.forEach(j -> jDTOS.add(subServicesMapper.convertToDTO(j)));
            return jDTOS;
      }

      @Override
      public ProjectResponse editSubServicesCustom(UpdateSubServicesDTO updateSubServicesDTO) {
            validation.checkText(updateSubServicesDTO.getName());
            Optional<SubServices> job;
            if (updateSubServicesDTO.getSubServicesId() != null) {
                  validation.checkPositiveNumber(updateSubServicesDTO.getSubServicesId());
                  job = subServicesService.findById(updateSubServicesDTO.getSubServicesId());
                  if (job.isEmpty())
                        throw new SubServicesIsNotExistException("this job dose not exist!");
                  job.get().setName(updateSubServicesDTO.getName());
            } else {
                  job = subServicesService.findByName(updateSubServicesDTO.getName());
                  if (job.isEmpty())
                        throw new SubServicesIsNotExistException("this job dose not exist!");
            }
            if (updateSubServicesDTO.getDescription().isEmpty() &&
                updateSubServicesDTO.getBasePrice() == 0L) {
                  throw new SubServicesIsNotExistException("change titles are empty!");
            } else if (!updateSubServicesDTO.getDescription().isEmpty()) {
                  validation.checkText(updateSubServicesDTO.getDescription());
                  job.get().setDescription(updateSubServicesDTO.getDescription());
            }
            if (updateSubServicesDTO.getBasePrice() != 0L) {
                  validation.checkPositiveNumber(updateSubServicesDTO.getBasePrice());
                  job.get().setBasePrice(updateSubServicesDTO.getBasePrice());
            }
            subServicesService.save(job.get());
            return new ProjectResponse("200", "UPDATED SUCCESSFUL");

      }

      @Override
      @Transactional(readOnly = true)
      public List<SpecialistResponseDTO> findAllSpecialist() {
            List<Specialist> specialists = specialistService.findAll();
            List<SpecialistResponseDTO> wDTOS = new ArrayList<>();
            if (!specialists.isEmpty())
                  specialists.forEach(w -> wDTOS.add(specialistMapper.convertToDTO(w)));
            return wDTOS;
      }

      @Override
      public ProjectResponse confirmSpecialist(Long workerId) {
            validation.checkPositiveNumber(workerId);
            Optional<Specialist> specialist = specialistService.findById(workerId);
            if (specialist.isEmpty())
                  throw new SpecialistIsNotExistException("this specialist does not exist!");
            if (specialist.get().getIsActive()) {
                  if (specialist.get().getStatus().equals(SpecialistStatus.CONFIRMED))
                        throw new SpecialistIsHoldsExistException("this specialist is currently certified!");
                  specialist.get().setStatus(SpecialistStatus.CONFIRMED);
            } else {
                  specialist.get().setIsActive(true);
            }
            specialistService.save(specialist.get());
            return new ProjectResponse("200", "UPDATED SUCCESSFUL");
      }

      @Override
      @Transactional(readOnly = true)
      public List<SubServicesResponseDTO> findAllSubServicessByMainService(Long mainServiceId) {
            validation.checkPositiveNumber(mainServiceId);
            if (mainServiceService.findById(mainServiceId).isEmpty())
                  throw new MainServicesIsNotExistException("this main service dose not exist!");
            return subServicesService.findByMainServiceId(mainServiceId);
      }

      @Override
      public ProjectResponse deActiveSpecialist(Long specialistId) {
            validation.checkPositiveNumber(specialistId);
            Optional<Specialist> specialist = specialistService.findById(specialistId);
            if (specialist.isEmpty())
                  throw new SpecialistIsNotExistException("this specialist does not exist!");
            if (!specialist.get().getIsActive())
                  throw new SpecialistIsHoldsExistException("this specialist is currently deActive");
            specialist.get().setIsActive(false);
            specialistService.save(specialist.get());
            return new ProjectResponse("200", "UPDATED SUCCESSFUL");

      }

      @Override
      public List<FilterUserResponseDTO> userFilter(FilterUserDTO userDTO) {
            List<FilterUserResponseDTO> filterUserResponseDTOList = new ArrayList<>();
            if (userDTO.getUserType().equals(CUSTOMER.name())) {
                  filterUserResponseDTOList.addAll(customerService.customerFilter(userDTO));
            }
            if (userDTO.getUserType().equals(SPECIALIST.name())) {
                  filterUserResponseDTOList.addAll(specialistService.specialistFilter(userDTO));
            }
            if (userDTO.getUserType().equals("ALL")) {
                  filterUserResponseDTOList.addAll(customerService.customerFilter(userDTO));
                  filterUserResponseDTOList.addAll(specialistService.specialistFilter(userDTO));
            }
            return filterUserResponseDTOList;
      }

      @Override
      public List<FilterOrderResponseDTO> orderFilter(FilterOrderDTO orderDTO) {
            return orderService.ordersFilter(orderDTO);
      }

      @Override
      public Optional<Admin> findByUsername(String email) {
            return repository.findByEmail(email);
      }
}

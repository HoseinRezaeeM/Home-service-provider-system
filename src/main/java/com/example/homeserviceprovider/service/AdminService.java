package com.example.homeserviceprovider.service;


import com.example.homeserviceprovider.domain.user.Admin;


import com.example.homeserviceprovider.dto.request.*;
import com.example.homeserviceprovider.dto.response.*;
import com.example.homeserviceprovider.service.base.BaseUsersService;



import java.util.List;

public interface AdminService extends BaseUsersService<Admin> {

      @Override
      void save(Admin admin);

      @Override
      List<Admin> findAll();

      ProjectResponse createMainService(MainServiceRequestDTO msDTO);

      ProjectResponse deleteMainService(String name);

      ProjectResponse addSubServices(SubServicesRequestDTO subServicesRequestDTO);

      ProjectResponse deleteSubServices(String name);

      ProjectResponse addSpecialistToSubServices(Long subServicesId, Long specialistId);

      ProjectResponse deleteSubServicesFromSpecialist(Long subServicesId, Long specialistId);

      List<MainServiceResponseDTO> findAllMainService();

      List<SubServicesResponseDTO> findAllSubServices();

      ProjectResponse editSubServicesCustom(UpdateSubServicesDTO updateSubServicesDTO);

      List<SpecialistResponseDTO> findAllSpecialist();

      ProjectResponse confirmSpecialist(Long specialistId);

      List<SubServicesResponseDTO> findAllSubServicessByMainService(Long mainServiceId);

      ProjectResponse deActiveSpecialist(Long specialistId);

      List<FilterUserResponseDTO> userFilter(FilterUserDTO userDTO);

      String addNewAdmin(AdminRegistrationDTO dto);

      List<FilterOrderResponseDTO> orderFilter(FilterOrderDTO orderDTO);



}

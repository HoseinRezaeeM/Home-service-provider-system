package com.example.homeserviceprovider.service;


import com.example.homeserviceprovider.domain.user.Specialist;
import com.example.homeserviceprovider.dto.request.*;
import com.example.homeserviceprovider.dto.response.*;
import com.example.homeserviceprovider.service.base.BaseUsersService;



import java.io.IOException;
import java.util.List;
import java.util.Optional;


public interface SpecialistService extends BaseUsersService<Specialist> {
      @Override
      void save(Specialist specialist);

      @Override
      void delete(Specialist specialist);

      @Override
      Optional<Specialist> findById(Long aLong);

      @Override
      List<Specialist> findAll();

      @Override
      Optional<Specialist> findByUsername(String email);

      ProjectResponse editPassword(ChangePasswordDTO changePasswordDTO, Long specialistId);

      List<FilterUserResponseDTO> specialistFilter(FilterUserDTO specialistDTO);

      String addNewSpecialist(SpecialistRegistrationDTO specialistRegistrationDTO) throws IOException;

      List<MainServiceResponseDTO> showAllMainServices();

      List<SubServicesResponseDTO> showSubServices(ChooseMainServiceDTO dto);


      List<LimitedOrderResponseDTO> showRelatedOrders(Long specialistId);

      ProjectResponse submitAnOffer(OfferRequestDTO offerRequestDTO, Long specialistId);

      double getSpecialistRate(Long specialistId);

      List<OfferResponseDTO> showAllOffersWaiting(Long specialistId);

      List<OfferResponseDTO> showAllOffersAccepted(Long specialistId);

      List<OfferResponseDTO> showAllOffersRejected(Long specialistId);

      Long getSpecialistCredit(Long specialistId);

      List<FilterUserResponseDTO> allSpecialist(FilterUserDTO userDTO);

}

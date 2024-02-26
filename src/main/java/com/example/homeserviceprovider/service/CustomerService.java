package com.example.homeserviceprovider.service;

import com.example.homeserviceprovider.domain.user.Customer;
import com.example.homeserviceprovider.domain.user.Users;
import com.example.homeserviceprovider.dto.request.*;
import com.example.homeserviceprovider.dto.response.*;
import com.example.homeserviceprovider.service.base.BaseUsersService;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;


import java.util.List;
import java.util.Optional;

public interface CustomerService extends BaseUsersService<Customer> {

      @Override
      void save(Customer customer);

      @Override
      void delete(Customer customer);

      @Override
      Optional<Customer> findById(Long aLong);

      @Override
      List<Customer> findAll();

      @Override
      Optional<Customer> findByUsername(String email);

      List<OfferResponseDTO> findOfferListByOrderIdBasedOnProposedPrice(Long orderId,Users users);

      List<OfferResponseDTO> findOfferListByOrderIdBasedOnSpecialistScore(Long orderId, Users users);

      List<FilterUserResponseDTO> customerFilter(FilterUserDTO customerDTO);

      String addNewCustomer(CustomerRegistrationDTO clientRegistrationDTO);

      ProjectResponse editPassword(ChangePasswordDTO changePasswordDTO, Long customerId);

      List<MainServiceResponseDTO> showAllMainServices();

      List<SubServicesResponseDTO> showSubServices(String mainServiceName);

      ProjectResponse addNewOrder(SubmitOrderDTO submitOrderDTO, Long customerId);

      ProjectResponse chooseSpecialistForOrder(Long offerId,Users users);

      ProjectResponse changeOrderStatusToStarted(Long orderId, Users users);

      ProjectResponse changeOrderStatusToDone(Long orderId,Users users);

      ProjectResponse registerComment(CommentRequestDTO commentRequestDTO, Users users);

      List<FilterOrderResponseDTO> showAllOrders(Long customerId);

      ProjectResponse changeOrderStatusToPaidByOnlinePayment(CustomerIdOrderIdDTO customerIdOrderIdDTO);

      ProjectResponse increaseCustomerCredit(Long id,Long price);

      ProjectResponse paidByInAppCredit(Long orderId, Users customer);

      ModelAndView payByOnlinePayment(Long orderId, Users users, Model model);

      ProjectResponse increaseAccountBalance(Long price, Long customerId);

      ProjectResponse addAddress(AddressDTO addressDTO, Long customerId);

      Long getCustomerCredit(Long customerId);

      List<FilterOrderResponseDTO> filterOrder(String orderStatus, Long customerid);


}

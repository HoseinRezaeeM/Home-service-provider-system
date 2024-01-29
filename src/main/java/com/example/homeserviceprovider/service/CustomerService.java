package com.example.homeserviceprovider.service;

import com.example.homeserviceprovider.domain.address.Address;
import com.example.homeserviceprovider.domain.comment.Comment;
import com.example.homeserviceprovider.domain.offer.Offer;
import com.example.homeserviceprovider.domain.order.Order;
import com.example.homeserviceprovider.domain.service.MainServices;
import com.example.homeserviceprovider.domain.service.SubServices;
import com.example.homeserviceprovider.domain.user.Customer;
import com.example.homeserviceprovider.domain.user.Users;
import com.example.homeserviceprovider.service.base.BaseUsersService;




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

      List<Offer> findOfferListByOrderIdBasedOnProposedPrice(Long orderId, Users users);

      List<Offer> findOfferListByOrderIdBasedOnSpecialistScore(Long orderId, Users users);


      void addNewCustomer(Customer customer);

      void editPassword(String newPassword, String confirmNewPassword, Long customerId);

      List<MainServices> showAllMainServices();

      List<SubServices> showSubServices(String mainServiceName);

      void addNewOrder(Order order, Long customerId);

      void chooseSpecialistForOrder(Long offerId, Users users);

      void changeOrderStatusToStarted(Long orderId, Users users);

      void changeOrderStatusToDone(Long orderId, Users users);

      void registerComment(Comment comment, Users users);


      List<Order> showAllOrder(Long customerId);


      void increaseCustomerCredit(Customer customer, Long price);

      void paidByInAppCredit(Long orderId, Users customer);



      void addAddress(Address address, Long customerId);

      Long getCustomerCredit(Long customerId);


}

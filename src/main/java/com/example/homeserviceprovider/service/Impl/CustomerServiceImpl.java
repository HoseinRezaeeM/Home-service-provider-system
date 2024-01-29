package com.example.homeserviceprovider.service.Impl;


import com.example.homeserviceprovider.base.service.impl.BaseEntityServiceImpl;
import com.example.homeserviceprovider.domain.address.Address;
import com.example.homeserviceprovider.domain.comment.Comment;
import com.example.homeserviceprovider.domain.offer.Offer;
import com.example.homeserviceprovider.domain.order.Order;
import com.example.homeserviceprovider.domain.order.enums.OrderStatus;
import com.example.homeserviceprovider.domain.service.MainServices;
import com.example.homeserviceprovider.domain.service.SubServices;
import com.example.homeserviceprovider.domain.user.Customer;
import com.example.homeserviceprovider.domain.user.Specialist;
import com.example.homeserviceprovider.domain.user.Users;
import com.example.homeserviceprovider.domain.user.enums.CustomerStatus;
import com.example.homeserviceprovider.exception.*;
import com.example.homeserviceprovider.repository.CustomerRepository;
import com.example.homeserviceprovider.service.*;
import com.example.homeserviceprovider.util.Validation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.homeserviceprovider.domain.offer.enums.OfferStatus.ACCEPTED;
import static com.example.homeserviceprovider.domain.offer.enums.OfferStatus.REJECTED;
import static com.example.homeserviceprovider.domain.order.enums.OrderStatus.*;
import static com.example.homeserviceprovider.domain.user.enums.CustomerStatus.HAS_NOT_ORDER_YET;



@Service
@Transactional
public class CustomerServiceImpl extends BaseEntityServiceImpl<Customer, Long, CustomerRepository>
       implements CustomerService {
      private static final String PRE_WRITTEN_MESSAGE =
             "This is a pre-written message: I have no particular opinion.";

      private final MainServiceService mainServiceService;
      private final SubServicesService subServicesService;
      private final OrderService orderService;
      private final OfferService offerService;
      private final CommentService commentService;



      private final Validation validation;

      public CustomerServiceImpl(CustomerRepository repository, MainServiceService mainServiceService, SubServicesService subServicesService,
                                 OrderService orderService, OfferService offerService,
                                 CommentService commentService, Validation validation) {
            super(repository);
            this.mainServiceService = mainServiceService;
            this.subServicesService = subServicesService;
            this.orderService = orderService;
            this.offerService = offerService;
            this.commentService = commentService;
            this.validation = validation;
      }

      @Override
      public Optional<Customer> findByUsername(String email) {
            return repository.findByEmail(email);
      }


      @Override
      public void addNewCustomer(Customer customer) {
            validation.checkEmail(customer.getEmail());
            if (repository.findByEmail(customer.getEmail()).isPresent())
                  throw new DuplicateEmailException("this Email already exist!");
            validation.checkPassword(customer.getPassword());
            validation.checkText(customer.getFirstname());
            validation.checkText(customer.getLastname());

            repository.save(customer);

      }

      @Override
      public void editPassword(String newPassword, String confirmNewPassword, Long clientId) {
            validation.checkPassword(newPassword);
            if (!newPassword.equals(confirmNewPassword))
                  throw new DuplicatePasswordException("this confirmNewPassword not match with newPassword!");
            Optional<Customer> customer = repository.findById(clientId);
            customer.get().setPassword(confirmNewPassword);

      }

      @Override
      @Transactional(readOnly = true)
      public List<MainServices> showAllMainServices() {
            List<MainServices> mainServices = new ArrayList<>();
            mainServices = mainServiceService.findAll();
            return mainServices.stream().toList();
      }

      @Override
      @Transactional(readOnly = true)
      public List<SubServices> showSubServices(String mainServiceName) {
            validation.checkBlank(mainServiceName);
            if (mainServiceService.findByName(mainServiceName).isEmpty())
                  throw new MainServicesIsNotExistException("this main service dose not exist!");
            return subServicesService.findByMainServiceName(mainServiceName);
      }

      @Override
      public void addNewOrder(Order order, Long customerId) {
           Customer customer = repository.findById(customerId).get();
            if (customer.getCustomerStatus().equals(CustomerStatus.NEW))
                  throw new CustomerStatusException("you can't sumbit order," +
                                                    "because your account is NEW," +
                                                    " please added Address and update your account.");
            validation.checkBlank(order.getAddress().getProvince());
            if ((customer.getAddressList().stream().filter(a ->
                   a.getProvince().equals(order.getAddress().getProvince()))).findFirst().isEmpty())
                  throw new AddressFormatException("you did not added such an address!");
            if (order.getEndTime().isBefore(LocalDateTime.now()))
                  throw new TimeException("passed this date!");
            if (order.getEndTime().isBefore(order.getExecutionTime()))
                  throw new TimeException("Time does not go back!");
            validation.checkBlank(order.getSubServices().getName());
            validation.checkPositiveNumber(order.getProposedPrice());
            validation.checkBlank(order.getDescription());
            Optional<SubServices> subServices = subServicesService.findByName(order.getSubServices().getName());
            if (subServices.isEmpty())
                  throw new SubServicesIsNotExistException("this subServices does not exist!");
            if (subServices.get().getBasePrice() > order.getProposedPrice())
                  throw new AmountLessExseption("this proposed price is less than base price of the subServices!");
            orderService.save(order);

      }

      @Override
      @Transactional(readOnly = true)
      public List<Order> showAllOrder(Long customerId) {
            List<Order> orderList = repository.findById(customerId).get().getOrderList();
            if (orderList.isEmpty())
                  return null;
            return orderList.stream().toList();
      }

      @Override
      @Transactional(readOnly = true)
      public Long getCustomerCredit(Long customerId) {
            Optional<Customer> customer = repository.findById(customerId);
            return customer.map(Customer::getCredit).orElse(null);
      }


      @Override
      public void increaseCustomerCredit(Customer customer, Long price) {
            validation.checkPositiveNumber(customer.getId());
            validation.checkPositiveNumber(price);
            Optional<Customer> dbClient = repository.findById(customer.getId());
            if (dbClient.isEmpty())
                  throw new CustomerNotExistException("not found user");
            dbClient.get().setCredit(dbClient.get().getCredit() + price);
            repository.save(dbClient.get());

      }

      @Override
      public void paidByInAppCredit(Long orderId, Users customer) {
            validation.checkPositiveNumber(orderId);
            validation.checkOwnerOfTheOrder(orderId, (Customer) customer);
            Optional<Customer> optionalCustomer = repository.findById(customer.getId());
            Optional<Order> order = orderService.findById(orderId);
            Long credit = optionalCustomer.get().getCredit();
            Long offerPrice = paymentPriceCalculator(orderId);
            if (credit < offerPrice)
                  throw new AmountLessExseption("not enough credit to pay in app");
            ((Customer) customer).setCredit(credit - offerPrice);
            repository.save(optionalCustomer.get());
      }


      @Override
      public void addAddress(Address address, Long customerId) {
            validation.checkAddress(address);
            Optional<Customer> customer = repository.findById(customerId);
            address.setCustomer(customer.get());
            customer.get().getAddressList().add(address);
            if (customer.get().getCustomerStatus().equals(CustomerStatus.NEW))
                  customer.get().setCustomerStatus(HAS_NOT_ORDER_YET);
            repository.save(customer.get());

      }

      private Long paymentPriceCalculator(Long orderId) {
            Optional<Order> order = orderService.findById(orderId);
            OrderStatus orderStatus = order.get().getOrderStatus();
            if (!orderStatus.equals(DONE)) {
                  if (orderStatus.equals(PAID))
                        throw new OrderStatusException(
                               "the cost of this order has already been \"PAID\"!");
                  else
                        throw new OrderStatusException(
                               "This order has not yet reached the payment stage, this order is in the " +
                               orderStatus + " stage!");
            }
            Optional<Offer> offer = order.get().getOfferList().stream().
                   filter(o -> o.getOfferStatus().equals(ACCEPTED)).findFirst();
            return offer.get().getProposedPrice();
      }

      @Override
      @Transactional(readOnly = true)
      public List<Offer> findOfferListByOrderIdBasedOnProposedPrice(Long orderId, Users users) {
            validation.checkPositiveNumber(orderId);
            validation.checkOwnerOfTheOrder(orderId, (Customer) users);
            List<Offer> list = offerService.findOfferListByOrderIdBasedOnProposedPrice(orderId);
            if (list.isEmpty())
                  return null;
            return list.stream().toList();
      }

      @Override
      @Transactional(readOnly = true)
      public List<Offer> findOfferListByOrderIdBasedOnSpecialistScore(Long orderId, Users users) {
            validation.checkPositiveNumber(orderId);
            validation.checkOwnerOfTheOrder(orderId, (Customer) users);
            List<Offer> list = offerService.findOfferListByOrderIdBasedOnSpecialistScore(orderId);
            if (list.isEmpty())
                  return null;
            return list.stream().toList();
      }

      @Override
      public void chooseSpecialistForOrder(Long offerId, Users users) {
            validation.checkPositiveNumber(offerId);
            validation.checkOfferBelongToTheOrder(offerId, (Customer) users);
            Optional<Offer> offer = offerService.findById(offerId);
            if (offer.get().getOfferStatus().equals(ACCEPTED))
                  throw new OfferStatusException(" this offer alrady accepted");
            else if (offer.get().getOfferStatus().equals(REJECTED))
                  throw new OfferStatusException(" this offer alrady rejected");
            else
                  orderService.chooseOffer(offer.get().getOrder(), offerId);

      }


      @Override
      public void changeOrderStatusToStarted(Long orderId, Users users) {
            validation.checkPositiveNumber(orderId);
            validation.checkOwnerOfTheOrder(orderId, (Customer) users);
            Optional<Order> order = orderService.findById(orderId);
            if (!order.get().getOrderStatus().equals(WAITING_FOR_SPECIALIST_TO_COME))
                  throw new OrderIsNotExistException
                         ("the status of this order is not yet \"WAITING FOR SPECIALIST TO COME\"!");
            order.get().getOfferList().forEach(o -> {
                  if (!o.getOfferStatus().equals(ACCEPTED))
                        if (!o.getExecutionTime().isBefore(LocalDateTime.now())) {
                              throw new TimeException("the specialist has not arrived at your place yet!");
                        }});
            order.get().setOrderStatus(STARTED);
            orderService.save(order.get());

      }

      @Override
      public void changeOrderStatusToDone(Long orderId, Users users) {
            validation.checkPositiveNumber(orderId);
            validation.checkOwnerOfTheOrder(orderId, (Customer) users);
            Optional<Order> order = orderService.findById(orderId);
            if (!order.get().getOrderStatus().equals(STARTED))
                  throw new OrderIsNotExistException
                         ("the status of this order is not yet \"STARTED\"!");
            Offer[] offer = new Offer[1];
            order.get().getOfferList().forEach(o -> {
                  if (o.getOfferStatus().equals(ACCEPTED))
                        offer[0] = o;
            });
            order.get().setOrderStatus(DONE);
            orderService.save(order.get());

      }

      @Override
      public void registerComment(Comment comment, Users users) {
            validation.checkPositiveNumber(comment.getOrder().getId());
            validation.checkOwnerOfTheOrder(comment.getOrder().getId(), (Customer) users);
            validation.checkScore(comment.getScore());
            if (comment.getTextComment().isBlank())
                  comment.setTextComment(PRE_WRITTEN_MESSAGE);
            else
                  validation.checkText(comment.getTextComment());
            Optional<Order> order = orderService.findById(comment.getOrder().getId());
            if (!order.get().getOrderStatus().equals(DONE))
                  throw new OrderIsNotExistException
                         ("the status of this order is not yet \"DONE\"!");
            Specialist specialist = order.get().getOfferList().stream().filter(o ->
                   o.getOfferStatus().equals(ACCEPTED)).findFirst().get().getSpecialist();
            comment.setOrder(order.get());
            commentService.save(comment);
            order.get().setComment(comment);
            orderService.save(order.get());

      }

}

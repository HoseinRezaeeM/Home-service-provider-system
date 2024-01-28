package com.example.homeserviceprovider.service.Impl;

import com.example.homeserviceprovider.base.service.impl.BaseEntityServiceImpl;
import com.example.homeserviceprovider.domain.offer.Offer;
import com.example.homeserviceprovider.domain.offer.enums.OfferStatus;
import com.example.homeserviceprovider.domain.order.Order;
import com.example.homeserviceprovider.domain.order.enums.OrderStatus;
import com.example.homeserviceprovider.domain.service.MainServices;
import com.example.homeserviceprovider.domain.service.SubServices;
import com.example.homeserviceprovider.domain.user.Specialist;
import com.example.homeserviceprovider.domain.user.enums.SpecialistStatus;
import com.example.homeserviceprovider.exception.*;
import com.example.homeserviceprovider.repository.SpecialistRepository;
import com.example.homeserviceprovider.service.*;


import com.example.homeserviceprovider.util.SaveImageToFile;
import com.example.homeserviceprovider.util.Validation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static com.example.homeserviceprovider.util.Validation.checkImage;

@Service
@Transactional
public class SpecialistServiceImpl extends BaseEntityServiceImpl<Specialist, Long, SpecialistRepository>
       implements SpecialistService {

      private final OfferService offerService;
      private final OrderService orderService;
      private final MainServiceService mainService;
      private final SubServicesService subServicesService;


      private final Validation validation;

      public SpecialistServiceImpl(SpecialistRepository repository, OfferService offerService, OrderService orderService,
                                   MainServiceService mainService, SubServicesService subServicesService, Validation validation) {
            super(repository);
            this.offerService = offerService;
            this.orderService = orderService;
            this.mainService = mainService;
            this.subServicesService = subServicesService;
            this.validation = validation;
      }

      @Override
      public void addNewSpecialist(Specialist specialist, String filePath) throws IOException {
            validation.checkEmail(specialist.getEmail());
            if (repository.findByEmail(specialist.getEmail()).isPresent())
                  throw new DuplicateEmailException("this Email already exist!");
            validation.checkPassword(specialist.getPassword());
            validation.checkText(specialist.getFirstname());
            validation.checkText(specialist.getLastname());
            repository.save(specialist);
            checkImage(filePath);
            repository.save(specialist);
            Path path = Paths.get(filePath);
            SaveImageToFile.saveImageToFile(specialist.getImage(),
                   "C:\\Users\\sp\\IdeaProjects\\home-service-provider\\src\\main\\resources\\imageUploaded\\"
                   + specialist.getFirstname()+""+specialist.getLastname() + ".jpg");

      }


      @Override
      @Transactional
      public void editPassword(String newPassword, String confirmNewPassword, Long specialistId) {
            validation.checkPassword(newPassword);
            if (!newPassword.equals(confirmNewPassword))
                  throw new DuplicatePasswordException("this confirmNewPassword not match with newPassword!");
            Optional<Specialist> specialist = repository.findById(specialistId);
            specialist.get().setPassword(confirmNewPassword);
            repository.editPassword(specialistId, confirmNewPassword);

      }

      @Override
      @Transactional(readOnly = true)
      public List<MainServices> showAllMainServices() {
            List<MainServices> mainServices = mainService.findAll();
            return mainServices.stream().toList();

      }

      @Override
      @Transactional(readOnly = true)
      public List<SubServices> showSubServices(MainServices mainServices) {
            Optional<MainServices> dbMainService = Optional.empty();
            if (!mainServices.getName().isBlank()) {
                  validation.checkText(mainServices.getName());
                  dbMainService = mainService.findByName(mainServices.getName());
            } else if (!mainServices.getId().toString().isBlank()) {
                  validation.checkPositiveNumber(mainServices.getId());
                  dbMainService = mainService.findById(mainServices.getId());
            }
            if (dbMainService.isEmpty())
                  throw new MainServicesIsNotExistException("this main service dose not exist!");
            return subServicesService.findByMainServiceId(dbMainService.get().getId());
      }

      @Override
      @Transactional(readOnly = true)
      public List<Order> showRelatedOrders(Long specialistId) {
            Optional<Specialist> specialist = repository.findById(specialistId);
            if (specialist.get().getSubServicesList().isEmpty())
                  throw new SpecialistNoAccessException("you do not have a subServices title!");
            List<Order> orderList = new ArrayList<>();
            specialist.get().getSubServicesList().forEach(subServicesName ->
                   orderList.addAll(orderService.findAllOrdersBySubServicesNameAndProvince(
                          subServicesName.getName(),specialist.get().getProvince())));
            return orderList;
      }

      @Override
      public void submitAnOffer(Offer offer, Long specialistId) {
            validation.checkPositiveNumber(offer.getOrder().getId());
            validation.checkPositiveNumber(offer.getProposedPrice());
            Optional<Specialist> specialistOptional = repository.findById(specialistId);
            if (!(specialistOptional.get().getStatus().equals(SpecialistStatus.CONFIRMED)))
                  throw new SpecialistNoAccessException("the status of specialist is not CONFIRMED");
            Optional<Order> order = orderService.findById(offer.getOrder().getId());
            if (order.isEmpty())
                  throw new OrderIsNotExistException("this order does not exist!");
            OrderStatus orderStatus = order.get().getOrderStatus();
            if (!(orderStatus.equals(OrderStatus.WAITING_FOR_SPECIALIST_SUGGESTION) ||
                  orderStatus.equals(OrderStatus.WAITING_FOR_SPECIALIST_SELECTION)))
                  throw new OrderStatusException("!this order has already accepted the offer");
            if (!(specialistOptional.get().getSubServicesList().contains(order.get().getSubServices())))
                  throw new SpecialistNoAccessException("this specialist does not have such subServices title!");
            if (offer.getEndTime().isBefore(offer.getExecutionTime()))
                  throw new TimeException("time does not go back!");
            if (offer.getExecutionTime().isBefore(order.get().getExecutionTime()))
                  throw new TimeException("no order has been in your proposed time for begin subServices!");
            if (order.get().getProposedPrice() > offer.getProposedPrice())
                  throw new AmountLessExseption("the proposed-price should not be lower than the order proposed-price!");
            offer.setSpecialist(specialistOptional.get());
            offer.setOrder(order.get());
            offerService.save(offer);
            if (orderStatus.equals(OrderStatus.WAITING_FOR_SPECIALIST_SUGGESTION))
                  order.get().setOrderStatus(OrderStatus.WAITING_FOR_SPECIALIST_SELECTION);
            orderService.save(order.get());

      }


      @Override
      @Transactional(readOnly = true)
      public Long getSpecialistCredit(Long specialistId) {
            validation.checkPositiveNumber(specialistId);
            Optional<Specialist> optionalSpecialist = repository.findById(specialistId);
            if (optionalSpecialist.isEmpty())
                  throw new SpecialistIsNotExistException("this specialist does not exist!");
            return optionalSpecialist.get().getCredit();
      }

      @Override
      @Transactional(readOnly = true)
      public List<Offer> showAllOffersWaiting(Long specialistId) {
            List<Offer> offers = offerService.findOffersBySpecialistIdAndOfferStatus(specialistId, OfferStatus.WAITING);
            return offers.stream().toList();
      }

      @Override
      @Transactional(readOnly = true)
      public List<Offer> showAllOffersAccepted(Long specialistId) {
            List<Offer> offers = offerService.findOffersBySpecialistIdAndOfferStatus(specialistId, OfferStatus.ACCEPTED);
            return offers.stream().toList();
      }

      @Override
      @Transactional(readOnly = true)
      public List<Offer> showAllOffersRejected(Long specialistId) {
            List<Offer> offers = offerService.findOffersBySpecialistIdAndOfferStatus(specialistId, OfferStatus.REJECTED);
            return offers.stream().toList();
      }

      @Override
      @Transactional(readOnly = true)
      public List<Specialist> findAll() {
            List<Specialist> specialistList = repository.findAll();
            if (specialistList.isEmpty())
                  throw new SpecialistIsNotExistException("there are no specialists!");
            return specialistList;
      }

      @Override
      public Optional<Specialist> findByUsername(String email) {
            return repository.findByEmail(email);
      }


}

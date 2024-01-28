package com.example.homeserviceprovider.service.Impl;

import com.example.homeserviceprovider.base.service.impl.BaseEntityServiceImpl;
import com.example.homeserviceprovider.domain.order.Order;
import com.example.homeserviceprovider.domain.order.enums.OrderStatus;
import com.example.homeserviceprovider.exception.SubServicesIsNotExistException;
import com.example.homeserviceprovider.repository.OrderRepository;
import com.example.homeserviceprovider.service.MainServiceService;
import com.example.homeserviceprovider.service.OrderService;

import com.example.homeserviceprovider.service.SubServicesService;
import com.example.homeserviceprovider.util.Validation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.homeserviceprovider.domain.offer.enums.OfferStatus.ACCEPTED;
import static com.example.homeserviceprovider.domain.offer.enums.OfferStatus.REJECTED;
import static com.example.homeserviceprovider.domain.order.enums.OrderStatus.*;


@Service
@Transactional
public class OrderServiceImpl extends BaseEntityServiceImpl<Order, Long, OrderRepository>
       implements OrderService {

      private final SubServicesService subServicesService;
      private final MainServiceService mainServiceService;


      private final Validation validation;

      public OrderServiceImpl(OrderRepository repository, SubServicesService subServicesService,
                              MainServiceService mainServiceService, Validation validation) {
            super(repository);
            this.subServicesService = subServicesService;
            this.mainServiceService = mainServiceService;
            this.validation = validation;
      }

      @Override
      public List<Order> findAllOrdersBySubServicesNameAndProvince(String subServicesName, String specialistProvince) {
            if (subServicesService.findByName(subServicesName).isEmpty())
                  throw new SubServicesIsNotExistException("this job does not exist!");
            List<Order> orders = repository.findBySubServiceNameAndOrderStatus(subServicesName,
                   WAITING_FOR_SPECIALIST_SUGGESTION,
                   WAITING_FOR_SPECIALIST_SELECTION);
            List<Order> relatedOrders = orders.stream().filter(o ->
                   o.getAddress().getProvince().equals(specialistProvince)).toList();
            return relatedOrders.stream().toList();
      }


      @Override
      public void chooseOffer(Order order, Long offerId) {
            order.getOfferList().forEach(o -> {
                  if (o.getId().equals(offerId))
                        o.setOfferStatus(ACCEPTED);
                  else
                        o.setOfferStatus(REJECTED);
            });
            order.setOrderStatus(WAITING_FOR_SPECIALIST_TO_COME);
            repository.save(order);
      }

      @Override
      public List<Order> findByCustomerEmailAndOrderStatus(String email, OrderStatus orderStatus) {
            return repository.findByCustomerEmailAndOrderStatus(email,orderStatus);
      }

}

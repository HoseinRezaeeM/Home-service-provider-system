package com.example.homeserviceprovider.service;

import com.example.homeserviceprovider.base.service.BaseEntityService;
import com.example.homeserviceprovider.domain.order.Order;
import com.example.homeserviceprovider.domain.order.enums.OrderStatus;


import java.util.List;
import java.util.Optional;

public interface OrderService extends BaseEntityService<Order,Long> {

    @Override
    void save(Order order);

    @Override
    void delete(Order order);

    @Override
    Optional<Order> findById(Long aLong);

    @Override
    List<Order> findAll();


    List<Order> findAllOrdersBySubServicesNameAndProvince(String subServicesName, String specialistProvince);

    void chooseOffer(Order order,Long offerId);

      List<Order> findByCustomerEmailAndOrderStatus(String email, OrderStatus orderStatus);


}

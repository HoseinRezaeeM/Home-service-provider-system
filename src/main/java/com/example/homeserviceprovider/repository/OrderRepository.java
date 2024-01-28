package com.example.homeserviceprovider.repository;

import com.example.homeserviceprovider.base.repository.BaseEntityRepository;
import com.example.homeserviceprovider.domain.order.Order;

import com.example.homeserviceprovider.domain.order.enums.OrderStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends BaseEntityRepository<Order, Long> {

      @Override
      boolean existsById(Long aLong);

      @Query("select o from Order o where o.customer.email = :customerEmail and o.orderStatus = :orderStatus")
      List<Order> findOrderListByCustomerEmailAndOrderStatus(String customerEmail, OrderStatus orderStatus);

//    @Modifying
//    @Query(" update Order o set o.orderStatus = :newOrderStatus where o.id = :orderId")
//    void changeOrderStatus(Long orderId, OrderStatus newOrderStatus);

      @Query(" select o from Order o where o.subServices.name= :subServiceName and" +
             " (o.orderStatus = :orderStatusOne or o.orderStatus = :orderStatusTwo)")
      List<Order> findBySubServiceNameAndOrderStatus(String subServiceName, OrderStatus orderStatusOne, OrderStatus orderStatusTwo);

      List<Order> findByCustomerEmailAndOrderStatus(String email, OrderStatus orderStatus);
}

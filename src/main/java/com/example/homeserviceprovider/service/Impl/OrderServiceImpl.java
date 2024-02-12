package com.example.homeserviceprovider.service.Impl;

import com.example.homeserviceprovider.base.service.impl.BaseEntityServiceImpl;
import com.example.homeserviceprovider.domain.order.Order;
import com.example.homeserviceprovider.domain.order.enums.OrderStatus;
import com.example.homeserviceprovider.domain.service.MainServices;
import com.example.homeserviceprovider.dto.request.FilterOrderDTO;
import com.example.homeserviceprovider.dto.response.FilterOrderResponseDTO;
import com.example.homeserviceprovider.dto.response.LimitedOrderResponseDTO;

import com.example.homeserviceprovider.exception.SubServicesIsNotExistException;
import com.example.homeserviceprovider.mapper.OrderMapper;
import com.example.homeserviceprovider.repository.OrderRepository;
import com.example.homeserviceprovider.service.MainServiceService;
import com.example.homeserviceprovider.service.OrderService;

import com.example.homeserviceprovider.service.SubServicesService;

import com.example.homeserviceprovider.util.Validation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;


import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.homeserviceprovider.domain.offer.enums.OfferStatus.ACCEPTED;
import static com.example.homeserviceprovider.domain.offer.enums.OfferStatus.REJECTED;
import static com.example.homeserviceprovider.domain.order.enums.OrderStatus.*;


@Service
public class OrderServiceImpl extends BaseEntityServiceImpl<Order, Long, OrderRepository>
       implements OrderService {

      private final SubServicesService subServicesService;
      private final MainServiceService mainServiceService;
      private final OrderMapper orderMapper;
      @PersistenceContext
      private final EntityManager entityManager;

      private final Validation validation;

      public OrderServiceImpl(OrderRepository repository, SubServicesService subServicesService, MainServiceService mainServiceService,
                              OrderMapper orderMapper, Validation validation, EntityManager entityManager) {
            super(repository);
            this.subServicesService = subServicesService;
            this.mainServiceService = mainServiceService;
            this.orderMapper = orderMapper;
            this.entityManager = entityManager;
            this.validation = validation;
      }

      @Override
      public List<LimitedOrderResponseDTO> findAllOrdersBySubServicesNameAndProvince(String subServicesName, String specialistProvince) {
            List<Order> dbOrders = repository.findBySubServiceNameAndOrderStatus(subServicesName,
                   WAITING_FOR_SPECIALIST_SUGGESTION,
                   WAITING_FOR_SPECIALIST_SELECTION);
            List<LimitedOrderResponseDTO> lorDTOS = new ArrayList<>();
            if (dbOrders.isEmpty())
                  return lorDTOS;
            List<Order> relatedOrders = dbOrders.stream().filter(o ->
                   o.getAddress().getProvince().equals(specialistProvince)).toList();
            if (relatedOrders.isEmpty())
                  return lorDTOS;
            relatedOrders.forEach(o ->
                   lorDTOS.add(orderMapper.convertToLimitedDto(o))
            );
            return lorDTOS;
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
            return repository.findByCustomerEmailAndOrderStatus(email, orderStatus);
      }

      @Override
      public List<FilterOrderResponseDTO> findAllOrdersBySubServicesName(String subServicesName) {
            if (subServicesService.findByName(subServicesName).isEmpty())
                  throw new SubServicesIsNotExistException("this job does not exist!");
            List<Order> orders = repository.findBySubServiceNameAndOrderStatus(subServicesName,
                   WAITING_FOR_SPECIALIST_SUGGESTION,
                   WAITING_FOR_SPECIALIST_SELECTION);
            List<FilterOrderResponseDTO> orDTOS = new ArrayList<>();
            orders.forEach(o -> orDTOS.add(orderMapper.convertToFilterDTO(o)));
            return orDTOS;
      }


      private void searchFilters(FilterOrderDTO dto, List<Predicate> predicateList, List<Order> resultList,
                                 CriteriaQuery<Order> orderCriteriaQuery, CriteriaBuilder criteriaBuilder,
                                 Root<Order> orderRoot) {
            createFilters(dto, predicateList, criteriaBuilder, orderRoot);
            Predicate[] predicates = new Predicate[predicateList.size()];
            predicateList.toArray(predicates);
            orderCriteriaQuery.select(orderRoot).where(predicates);
            resultList.addAll(entityManager.createQuery(orderCriteriaQuery).getResultList());
      }

      private void createFilters(FilterOrderDTO orderDTO, List<Predicate> predicateList,
                                 CriteriaBuilder criteriaBuilder, Root<Order> orderRoot) {
            if (orderDTO.getDescription() != null) {
                  validation.checkText(orderDTO.getDescription());
                  String description = "%" + orderDTO.getDescription() + "%";
                  predicateList.add(criteriaBuilder.like(orderRoot.get("description"),
                         description));
            }
            if (orderDTO.getOrderStatus() != null) {
                  predicateList.add(criteriaBuilder.equal(orderRoot.get("orderStatus"),orderDTO.getOrderStatus()));
            }
            if (orderDTO.getJobId() != null) {
                  validation.checkPositiveNumber(orderDTO.getJobId());
                  predicateList.add(criteriaBuilder.equal(orderRoot.get("job"),
                         subServicesService.findById(orderDTO.getJobId()).get()));
            }
            if (orderDTO.getMinProposedPrice() == null && orderDTO.getMaxProposedPrice() != null)
                  orderDTO.setMinProposedPrice(0L);
            if (orderDTO.getMinProposedPrice() != null && orderDTO.getMaxProposedPrice() == null)
                  orderDTO.setMaxProposedPrice(Long.MAX_VALUE);
            if (orderDTO.getMinProposedPrice() != null && orderDTO.getMaxProposedPrice() != null)
                  predicateList.add(criteriaBuilder.between(orderRoot.get("proposedPrice"),
                         orderDTO.getMinProposedPrice(), orderDTO.getMaxProposedPrice()));

            if (orderDTO.getMinOrderRegistrationDate() == null && orderDTO.getMaxOrderRegistrationDate() != null)
                  orderDTO.setMinOrderRegistrationDate(LocalDateTime.now().minusYears(5));
            if (orderDTO.getMinOrderRegistrationDate() != null && orderDTO.getMaxOrderRegistrationDate() == null)
                  orderDTO.setMaxOrderRegistrationDate(LocalDateTime.now());
            if (orderDTO.getMinOrderRegistrationDate() != null && orderDTO.getMaxOrderRegistrationDate() != null)
                  predicateList.add(criteriaBuilder.between(orderRoot.get("registrationTime"),
                         orderDTO.getMinOrderRegistrationDate(),
                         orderDTO.getMaxOrderRegistrationDate()));
      }

      @Override
      public List<FilterOrderResponseDTO> ordersFilter(FilterOrderDTO dto) {
            List<FilterOrderResponseDTO> forDTOS = new ArrayList<>();
            List<Long> dbJobsId = new ArrayList<>();
            if (dto.getMainServiceId() != null) {
                  Optional<MainServices> dbMainService = mainServiceService.findById(dto.getMainServiceId());
                  if (dbMainService.isEmpty()) return forDTOS;
                  else {
                        dbMainService.get().getSubServicesList().forEach(j -> dbJobsId.add(j.getId()));
                        if (dto.getJobId() != null)
                              if (!dbJobsId.contains(dto.getJobId())) return forDTOS;
                  }
            } else if (dto.getJobId() != null)
                  dbJobsId.add(dto.getJobId());
            List<Predicate> predicateList = new ArrayList<>();
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Order> orderCriteriaQuery = criteriaBuilder.createQuery(Order.class);
            Root<Order> orderRoot = orderCriteriaQuery.from(Order.class);
            List<Order> resultList = new ArrayList<>();
            if (dbJobsId.isEmpty()) dbJobsId.add(null);
            dbJobsId.forEach(ji -> {
                  dto.setJobId(ji);
                  searchFilters(dto, predicateList, resultList, orderCriteriaQuery, criteriaBuilder, orderRoot);
            });
            if (!resultList.isEmpty()) resultList.forEach(rl -> forDTOS.add(orderMapper.convertToFilterDTO(rl)));
            return forDTOS;
      }

}

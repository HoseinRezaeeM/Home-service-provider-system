package com.example.homeserviceprovider.mapper;


import com.example.homeserviceprovider.domain.order.Order;
import com.example.homeserviceprovider.domain.service.SubServices;
import com.example.homeserviceprovider.domain.user.Customer;
import com.example.homeserviceprovider.dto.request.CustomerIdOrderIdDTO;
import com.example.homeserviceprovider.dto.request.SubmitOrderDTO;
import com.example.homeserviceprovider.dto.response.FilterOrderResponseDTO;
import com.example.homeserviceprovider.dto.response.LimitedOrderResponseDTO;
import com.example.homeserviceprovider.dto.response.OrderResponseDTO;
import com.example.homeserviceprovider.util.CustomDuration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.example.homeserviceprovider.domain.offer.enums.OfferStatus.ACCEPTED;
import static com.example.homeserviceprovider.domain.order.enums.OrderStatus.WAITING_FOR_SPECIALIST_SELECTION;
import static com.example.homeserviceprovider.domain.order.enums.OrderStatus.WAITING_FOR_SPECIALIST_SUGGESTION;


@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final CustomDuration customDuration;

    public Order convertToNewOrder(SubmitOrderDTO soDTO, Customer customer, SubServices subServices) {
        return new Order(
                soDTO.getWorkStartDate(),
                soDTO.getWorkEndDate(),
                soDTO.getProposedPrice(),
                soDTO.getDescription(),customer, subServices,
                customer.getAddressList().stream().filter(a ->
                        a.getProvince().equals(soDTO.getAddressProvince())).findFirst().get()


        );
    }

    public OrderResponseDTO convertToDTO(Order order) {
        return new OrderResponseDTO(
                order.getAddress(),
                order.getDescription(),
                order.getProposedPrice(),
                order.getOrderStatus(),
                order.getOfferList(),
                order.getSubServices().getName(),
                order.getSubServices().getMainServices().getName(),
                order.getExecutionTime(),
                order.getEndTime(),
                customDuration.getDuration(order.getExecutionTime(),
                        order.getEndTime(), order.getClass().getName())
        );
    }

    public FilterOrderResponseDTO convertToFilterDTO(Order order) {
        FilterOrderResponseDTO fdto = new FilterOrderResponseDTO(
                (" ,Address: " + order.getAddress().getProvince() +
                 " ," + order.getAddress().getCity() +
                 " ," + order.getAddress().getAvenue() +
                 " ," + order.getAddress().getMoreDescription() +
                 " ,HouseNumber: " + order.getAddress().getHouseNumber() +
                 " ,PostalCode: " + order.getAddress().getPostalCode()),
                order.getDescription(),
                order.getProposedPrice(),
                order.getOrderStatus(),
                order.getSubServices().getName(),
                order.getSubServices().getMainServices().getName(),
                order.getExecutionTime(),
                customDuration.getDuration(
                        order.getExecutionTime(),
                        order.getEndTime(),
                        order.getClass().getName())

        );
        if (order.getOrderStatus().equals(WAITING_FOR_SPECIALIST_SUGGESTION) ||
            order.getOrderStatus().equals(WAITING_FOR_SPECIALIST_SELECTION))
            fdto.setAcceptedOfferId(-1L);
        else
            fdto.setAcceptedOfferId(
                    order.getOfferList().stream().filter(o ->
                            o.getOfferStatus().equals(ACCEPTED)).findFirst().get().getId()
            );
        return fdto;
    }

    public LimitedOrderResponseDTO convertToLimitedDto(Order order) {
        return new LimitedOrderResponseDTO(
                (order.getAddress().getProvince() + " ," +
                 order.getAddress().getCity() + " ," +
                 order.getAddress().getAvenue() + " , ..."
                ),
                order.getDescription(),
                order.getProposedPrice(),
                order.getOrderStatus(),
                order.getSubServices().getName(),
                order.getSubServices().getMainServices().getName(),
                order.getExecutionTime(),
                order.getEndTime(),
                customDuration.getDuration(order.getExecutionTime(),
                        order.getEndTime(), order.getClass().getName())
        );
    }
}

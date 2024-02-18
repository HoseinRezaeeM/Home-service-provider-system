package com.example.homeserviceprovider.controller;

import com.example.homeserviceprovider.domain.user.Users;
import com.example.homeserviceprovider.dto.request.*;
import com.example.homeserviceprovider.dto.response.*;

import com.example.homeserviceprovider.service.CustomerService;
import com.example.homeserviceprovider.util.Validation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {


    private final CustomerService customerServices;
    private final Validation validation;


    @PutMapping("/add-address")
    public ResponseEntity<ProjectResponse> addAddress(
           @RequestBody AddressDTO addressDTO, Authentication authentication) {
        return ResponseEntity.ok().body(customerServices
                .addAddress(addressDTO, ((Users) authentication.getPrincipal()).getId()));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ProjectResponse> changePassword(
           @RequestBody ChangePasswordDTO changePasswordDTO, Authentication authentication) {
        return ResponseEntity.ok().body(customerServices
                .editPassword(changePasswordDTO, ((Users) authentication.getPrincipal()).getId()));
    }

    @GetMapping("/show-all-main-services")
    public List<MainServiceResponseDTO> findAllMainService() {
        return customerServices.showAllMainServices();
    }

    @GetMapping("/show-all-subServices-by-service/{mainServiceName}")
    public List<SubServicesResponseDTO> findAllSubServices(@PathVariable String mainServiceName) {
        return customerServices.showSubServices(mainServiceName);
    }

    @PostMapping("/submit-order")
    public ResponseEntity<ProjectResponse> submitOrder(
           @RequestBody SubmitOrderDTO submitOrderDTO, Authentication authentication) {
        return ResponseEntity.ok().body(customerServices.addNewOrder(
                submitOrderDTO, ((Users) authentication.getPrincipal()).getId()));
    }

    @GetMapping("/show-all-orders")
    public List<FilterOrderResponseDTO> showAllOrders(Authentication authentication) {
        return customerServices.showAllOrders(
                ((Users) authentication.getPrincipal()).getId());
    }

    @GetMapping("/filter-order/{orderStatus}")
    public List<FilterOrderResponseDTO> filterOrder(
            @PathVariable String orderStatus, Authentication authentication) {
        return customerServices.filterOrder(orderStatus,
                ((Users) authentication.getPrincipal()).getId());
    }

    @GetMapping("/show-my-credit")
    public Long showCustomerCredit(Authentication authentication) {
        return customerServices.getCustomerCredit(
                ((Users) authentication.getPrincipal()).getId());
    }

    @GetMapping("/show-all-offer-by-score/{orderId}")
    public List<OfferResponseDTO> showAllOfferForOrderByWorkerScore(
            @PathVariable Long orderId, Authentication authentication) {
        return customerServices.findOfferListByOrderIdBasedOnSpecialistScore(
                orderId, ((Users) authentication.getPrincipal()));
    }

    @GetMapping("/show-all-offer-by-price/{orderId}")
    public List<OfferResponseDTO> showAllOfferForOrderByProposedPrice(
            @PathVariable Long orderId, Authentication authentication) {
        return customerServices.findOfferListByOrderIdBasedOnProposedPrice(
                orderId, ((Users) authentication.getPrincipal()));
    }

    @PutMapping("/choose-offer-for-order/{offerId}")
    public ResponseEntity<ProjectResponse> chooseSpecialistForOrder(
            @PathVariable Long offerId, Authentication authentication) {
        return ResponseEntity.ok().body(customerServices.chooseSpecialistForOrder(
                offerId, ((Users) authentication.getPrincipal())));
    }

    @PutMapping("/order-started/{orderId}")
    public ResponseEntity<ProjectResponse> orderStarted(
            @PathVariable Long orderId, Authentication authentication) {
        return ResponseEntity.ok().body(customerServices.changeOrderStatusToStarted(
                orderId, ((Users) authentication.getPrincipal())));
    }

    @PutMapping("/order-doned/{orderId}")
    public ResponseEntity<ProjectResponse> orderDoned(
            @PathVariable Long orderId, Authentication authentication) {
        return ResponseEntity.ok().body(customerServices.changeOrderStatusToDone(
                orderId, ((Users) authentication.getPrincipal())));
    }

    @PostMapping("/register-comment")
    public ResponseEntity<ProjectResponse> registerComment(
           @RequestBody CommentRequestDTO commentRequestDTO, Authentication authentication) {
        return ResponseEntity.ok().body(customerServices.registerComment(
                commentRequestDTO, ((Users) authentication.getPrincipal())));
    }

    @PutMapping("/paid-in-app-credit/{orderId}")
    public ResponseEntity<ProjectResponse> payByInApp(
            @PathVariable Long orderId, Authentication authentication) {
        return ResponseEntity.ok().body(customerServices.paidByInAppCredit(
                orderId, ((Users) authentication.getPrincipal())));
    }

    @Transactional
    @GetMapping("/pay-online-payment/{orderId}")
    public ModelAndView payByOnlinePayment(
            @PathVariable Long orderId, Model model, Authentication authentication) {
        return customerServices.payByOnlinePayment(
                orderId, ((Users) authentication.getPrincipal()), model);
    }

    @Transactional
    @GetMapping("/increase-account-balance/{price}")
    public ModelAndView increaseAccountBalance(
            @PathVariable Long price, Model model, Authentication authentication) {
        return customerServices.increaseAccountBalance(
                price, ((Users) authentication.getPrincipal()).getId(), model);
    }

    @PostMapping("/send-payment-info")
    public ResponseEntity<ProjectResponse> paymentInfo(
            @ModelAttribute @Validated PaymentRequestDTO dto) {
        validation.checkPaymentRequest(dto);
        return ResponseEntity.ok().body(customerServices.changeOrderStatusToPaidByOnlinePayment(
                dto.getCustomerIdOrderIdDTO()));
    }
    @PostMapping("/send-increase-balance-info")
    public ResponseEntity<ProjectResponse> increaseBalanceInfo(
            @ModelAttribute @Validated BalanceRequestDTO dto) {
        validation.checkBalanceRequest(dto);
        return ResponseEntity.ok().body(customerServices.increaseCustomerCredit(
                dto.getCustomerIdPriceDTO()));
    }

}

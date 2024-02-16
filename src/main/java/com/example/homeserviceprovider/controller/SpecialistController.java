package com.example.homeserviceprovider.controller;


import com.example.homeserviceprovider.domain.user.Specialist;
import com.example.homeserviceprovider.domain.user.Users;
import com.example.homeserviceprovider.dto.request.ChangePasswordDTO;
import com.example.homeserviceprovider.dto.request.ChooseMainServiceDTO;
import com.example.homeserviceprovider.dto.request.OfferRequestDTO;
import com.example.homeserviceprovider.dto.response.*;
import com.example.homeserviceprovider.service.SpecialistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/specialist")
@RequiredArgsConstructor
public class SpecialistController {

    private final SpecialistService specialistService;

    @PutMapping("/change-password")
    public ResponseEntity<ProjectResponse> changePassword(
           @RequestBody ChangePasswordDTO changePasswordDTO, Authentication authentication) {
        return ResponseEntity.ok().body(specialistService
                .editPassword(changePasswordDTO,
                        ((Users) authentication.getPrincipal()).getId()));
    }

    @GetMapping("/show-all-main-services")
    public List<MainServiceResponseDTO> findAllMainService() {
        return specialistService.showAllMainServices();
    }

    @GetMapping("/show-all-subServices-by-service")
    public List<SubServicesResponseDTO> findAllJobs(
            @RequestBody ChooseMainServiceDTO chooseMainServiceDTO) {
        return specialistService.showSubServices(chooseMainServiceDTO);
    }

    @GetMapping("/show-related-orders")
    public List<LimitedOrderResponseDTO> viewOrdersRelatedToWorker(
            Authentication authentication) {
        return specialistService.showRelatedOrders(((Users) authentication.getPrincipal()).getId());
    }

    @PostMapping("/submit-offer-for-order")
    public ResponseEntity<ProjectResponse> submitOfferForOrder(
           @RequestBody OfferRequestDTO offerRequestDTO, Authentication authentication) {
        return ResponseEntity.ok().body(specialistService.submitAnOffer(
                offerRequestDTO, ((Users) authentication.getPrincipal()).getId()));
    }

    @GetMapping("/show-specialist-score")
    public double viewSpecialistScore(Authentication authentication) {
        Specialist specialist = (Specialist) authentication.getPrincipal();
        System.out.println(specialist.getScore());
        return specialistService.getSpecialistRate(specialist.getId());
    }

    @GetMapping("/show-specialist-credit")
    public Long viewWorkerCredit(Authentication authentication) {
        Specialist specialist = (Specialist) authentication.getPrincipal();
        return specialistService.getSpecialistCredit(specialist.getId());
    }

    @GetMapping("/show-all-offers-waiting")
    public List<OfferResponseDTO> viewAllWaitingOffers(Authentication authentication) {
        Specialist specialist = (Specialist) authentication.getPrincipal();
        return specialistService.showAllOffersWaiting(specialist.getId());
    }

    @GetMapping("/show-all-offers-accepted")
    public List<OfferResponseDTO> viewAllAcceptedOffers(Authentication authentication) {
        Specialist specialist = (Specialist) authentication.getPrincipal();
        return specialistService.showAllOffersAccepted(specialist.getId());
    }

    @GetMapping("/show-all-offers-rejected")
    public List<OfferResponseDTO> viewAllRejectedOffers(Authentication authentication) {
        Specialist specialist = (Specialist) authentication.getPrincipal();
        return specialistService.showAllOffersRejected(specialist.getId());
    }


}

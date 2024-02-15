package com.example.homeserviceprovider.mapper;

import com.example.homeserviceprovider.domain.offer.Offer;
import com.example.homeserviceprovider.dto.request.OfferRequestDTO;
import com.example.homeserviceprovider.dto.response.OfferResponseDTO;
import com.example.homeserviceprovider.util.CustomDuration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OfferMapper {

    private final CustomDuration customDuration;

    public OfferResponseDTO convertToDTO(Offer offer) {
        return new OfferResponseDTO(
                offer.getId(),
                offer.getSpecialist().getId(),
                offer.getSpecialist().getUsername(),
                offer.getOrder().getId(),
                offer.getProposedPrice(),
                offer.getOfferStatus(),
                offer.getExecutionTime(),
                offer.getEndTime(),
                customDuration.getDuration(offer.getExecutionTime(),
                        offer.getEndTime(), offer.getClass().getName())
        );
    }

    public Offer convertToNewOffer(OfferRequestDTO dto) {
        return new Offer(
                dto.getProposedStartDate(),
                dto.getProposedEndDate(),
                dto.getOfferProposedPrice()
        );
    }

}

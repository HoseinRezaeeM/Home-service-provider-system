package com.example.homeserviceprovider.service;

import com.example.homeserviceprovider.base.service.BaseEntityService;
import com.example.homeserviceprovider.domain.offer.Offer;
import com.example.homeserviceprovider.domain.offer.enums.OfferStatus;


import java.util.List;
import java.util.Optional;

public interface OfferService extends BaseEntityService<Offer,Long> {

    @Override
    void save(Offer offer);

    @Override
    void delete(Offer offer);

    @Override
    Optional<Offer> findById(Long aLong);

    @Override
    List<Offer> findAll();

    List<Offer> findOfferListByOrderIdBasedOnProposedPrice(Long orderId);

    List<Offer> findOfferListByOrderIdBasedOnSpecialistScore(Long orderId);


    List<Offer> findOffersBySpecialistIdAndOfferStatus(Long specialistId, OfferStatus offerStatus);

    Optional<Offer> acceptedOffer(Long orderId);
}
